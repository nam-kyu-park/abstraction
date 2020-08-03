package packet

import (
	"bytes"
	"encoding/binary"
	"encoding/gob"
	"encoding/hex"
	"fmt"
	"io"
	"log"
	"net"
)

type Authorization struct {
	SessionID string
	State     string
}

func (this *Authorization) New(id string, state string) {
	this.SessionID = id
	this.State = state
}

const (
	STX byte = 0xEA
	TKN byte = 0xEB
	ETX byte = 0xEC
)

const (
	SessionStart uint16 = 0x01
	SessionJoin  uint16 = 0x02
	Collector    uint16 = 0x03
)

type Packet struct {
	Service uint16
	Type    uint16
	Length  uint16
	//Point   []*DataPoint
	Point []DataPoint
	//Point []interface{}
}

type DataPoint struct {
	Name  string
	Value string
}

func New() *Packet {
	instance := new(Packet)
	instance.Service = 0
	instance.Type = 0
	instance.Length = 0
	instance.Point = make([]DataPoint, 0, 100)
	return instance
}

func (this *Packet) Append(points ...DataPoint) {
	for _, point := range points {
		this.Length++
		this.Point = append(this.Point, point)
	}
}

func (this *Packet) Encode() []byte {
	buffer, err := Encode(this)
	if err != nil {
		return nil
	}
	return buffer
}

func Registry(c interface{}) {
	gob.Register(c)
}

func Encode(p *Packet) ([]byte, error) {
	gob.Register(&Packet{})
	gob.Register(&DataPoint{})

	var buffer bytes.Buffer
	enc := gob.NewEncoder(&buffer)
	err := enc.Encode(*p)
	if err != nil {
		return nil, err
	}
	return buffer.Bytes(), err
}

func Decode(b *[]byte) (*Packet, error) {
	gob.Register(&Packet{})
	gob.Register(&DataPoint{})

	packet := New()
	buffer := new(bytes.Buffer)
	buffer.Write(*b)
	dec := gob.NewDecoder(buffer)
	err := dec.Decode(packet)

	return packet, err
}

func Build(data []byte) []byte {

	var size int
	length := len(data)

	// SIZE = STX + TOCKEN + LENGTH(int64) + LENGTH(DATA) + CHECKSUM(int64) + ETX
	size = (2 + 2 + 8 + length + 2 + 2)

	stx := []byte(hex.EncodeToString([]byte{STX}))
	tkn := []byte(hex.EncodeToString([]byte{TKN}))
	etx := []byte(hex.EncodeToString([]byte{ETX}))

	buffer := make([]byte, 0, size)
	buffer = append(buffer, stx...)
	buffer = append(buffer, tkn...)
	buffer = append(buffer, Uint64ToBytes(uint64(length), 8)...)
	buffer = append(buffer, data...)
	buffer = append(buffer, Uint16ToBytes(uint16(CheckSum(data)), 2)...)
	buffer = append(buffer, etx...)

	//fmt.Printf("%s", hex.Dump(buffer[:size]))
	return buffer[:size]
}

func CheckSum(msg []byte) uint16 {
	sum := 0

	// assume even for now
	for n := 1; n < len(msg)-1; n += 2 {
		sum += int(msg[n])*256 + int(msg[n+1])
	}
	sum = (sum >> 16) + (sum & 0xffff)
	sum += (sum >> 16)
	var answer uint16 = uint16(^sum)
	return answer
}

func Debug(b []byte) string {
	return hex.Dump(b)
}

func ReadHeader(conn net.Conn, buf *[]byte) error {
	if buf == nil {
		return fmt.Errorf("buffer null pointor")
	}

	if n, err := conn.Read((*buf)[:4]); n != 4 || err == io.EOF {
		return err
	}

	// Check Head
	if err := CheckHeader((*buf)[:4]); err == false {
		return fmt.Errorf("buffer null pointor")
	}

	return nil
}

func CheckHeader(head []byte) bool {
	if head == nil || len(head) < 4 {
		return false
	}

	dst := make([]byte, hex.DecodedLen(len(head)))
	n, err := hex.Decode(dst, head)
	if err != nil {
		log.Fatal(err)
	}

	if n := bytes.Compare(dst[:n], []byte{STX, TKN}); n != 0 {
		return false
	}

	return true
}

func ReadLength(conn net.Conn, buf *[]byte) int {
	if buf == nil {
		return 0
	}

	if n, err := conn.Read((*buf)[:8]); n != 8 || err == io.EOF {
		return 0
	}

	size := binary.BigEndian.Uint64((*buf)[:8])

	return int(size)
}

func ReadBody(conn net.Conn, buf *[]byte, size int, codeBuf *bytes.Buffer) int {
	if buf == nil || size < 1 {
		return 0
	}

	var length int = 0
	for length < size {
		n, err := conn.Read((*buf)[:size])
		if err == io.EOF {
			return length
		}

		codeBuf.Write((*buf)[:n])

		length += n
	}

	return size
}

func ReadChecksum(conn net.Conn, buf *[]byte) uint16 {
	if buf == nil {
		return 0
	}

	n, err := conn.Read((*buf)[:2])
	if n != 2 || err == io.EOF {
		return 0
	}

	return BytesToUint16((*buf)[:2]...)
}

func ReadTail(conn net.Conn, buf *[]byte) error {
	if buf == nil {
		return fmt.Errorf("buffer null pointor")
	}

	if n, err := conn.Read((*buf)[:2]); n != 2 || err == io.EOF {
		return err
	}

	// Check Head
	if err := CheckTail((*buf)[:2]); err == false {
		return fmt.Errorf("buffer null pointor")
	}

	return nil
}

func CheckTail(head []byte) bool {
	if head == nil || len(head) < 2 {
		return false
	}

	dst := make([]byte, hex.DecodedLen(len(head)))
	n, err := hex.Decode(dst, head)
	if err != nil {
		log.Fatal(err)
	}

	if n := bytes.Compare(dst[:n], []byte{ETX}); n != 0 {
		return false
	}

	return true
}

// BytesTouint64 converts []byte to uint64
func BytesToUint64(bytes ...byte) uint64 {
	padding := make([]byte, 8-len(bytes))
	i := binary.BigEndian.Uint64(append(padding, bytes...))
	return i
}

// Uint64Tobytes converts uint64 to []byte
func Uint64ToBytes(i uint64, size int) []byte {
	bytes := make([]byte, 8)
	binary.BigEndian.PutUint64(bytes, i)
	return bytes[8-size : 8]
}

// BytesTouint16 converts []byte to uint16
func BytesToUint16(bytes ...byte) uint16 {
	return binary.BigEndian.Uint16(bytes)
}

// Uint16Tobytes converts uint16 to []byte
func Uint16ToBytes(i uint16, size int) []byte {
	bytes := make([]byte, 2)
	binary.BigEndian.PutUint16(bytes, i)
	return bytes
}

func init() {
	gob.Register(&Packet{})
}
