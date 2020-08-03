package kr.co.sptek.abstraction.module.ftp;

import com.jcraft.jsch.*;
import kr.co.sptek.abstraction.util.PathUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Vector;

@Component
public class FTPConnector {

    private static final Logger logger = LogManager.getLogger(FTPConnector.class);

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    private static final String PARENT_DIR = "..";

    private static final String CURRENT_DIR = ".";

    public FTPConnector() {
    }

    public void init(String host, String port, String userName, String password) throws JSchException {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(userName, host, Integer.parseInt(port));
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        channelSftp = (ChannelSftp) channel;
    }

    public String getHome() throws SftpException {
        return channelSftp.getHome();
    }

    public Vector<ChannelSftp.LsEntry> getFileList(String path) {
        Vector<ChannelSftp.LsEntry> list = null;
        try {
            list = channelSftp.ls(path);
        } catch (SftpException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }
//
//    public void upload(String dir, String file) throws SftpException {
//        this.cd(dir);
//        FileInputStream in = null;
//        try {
//            File fp = new File(file);
//            in = new FileInputStream(fp);
//            channelSftp.put(in, fp.getName());
//        } catch (SftpException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (in != null)
//                    in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void upload(String dir, String file) throws Exception {
        _upload(FilenameUtils.separatorsToUnix(dir), FilenameUtils.separatorsToUnix(file), channelSftp);
    }

    private void _upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception{

        File file = new File(uploadFile);
        if(file.exists()){

            try {
                Vector content = sftp.ls(directory);
                if(content == null){
                    sftp.mkdir(directory);
                    //System.out.println("mkdir:" + directory);
                }
            } catch (SftpException e) {
                sftp.mkdir(directory);
            }
            sftp.cd(directory);
            System.out.println("directory: " + directory);
            if(file.isFile()){
                InputStream ins = new FileInputStream(file);

                sftp.put(ins, new String(file.getName().getBytes(), StandardCharsets.UTF_8));

            }else{
                File[] files = file.listFiles();
                for (File file2 : files) {
                    String dir = file2.getAbsolutePath();
                    if(file2.isDirectory()){
                        String str = dir.substring(dir.lastIndexOf(File.separator));
                        directory = directory + str;
                    }
                   // System.out.println("directory is :" + directory);
                    _upload(FilenameUtils.separatorsToUnix(directory),FilenameUtils.separatorsToUnix(dir),sftp);
                }
            }
        }
    }

    public void Trace(String path) {
        Vector<ChannelSftp.LsEntry> vector =  getFileList(path);
        System.out.println("The iterator values are: ");
        Iterator value = vector.iterator();
        while (value.hasNext()) {
            System.out.println(value.next());
        }
    }

    public void cd(String dir) throws SftpException {
        channelSftp.cd(FilenameUtils.separatorsToUnix(dir));
    }

    public boolean stat(String dir) throws SftpException {
        try {
             SftpATTRS attrs = channelSftp.stat(FilenameUtils.separatorsToUnix(dir));
            return (attrs != null);
        }
        catch (SftpException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    public void mkdir(String dir) throws SftpException {
        if(!stat(dir))
            channelSftp.mkdir(FilenameUtils.separatorsToUnix(dir));
    }

    public void download(String dir, String downloadFileName, String path) throws SftpException {
        if (isDirectory(PathUtil.join(dir, downloadFileName))) {
            downloadDirectory(PathUtil.join(dir, downloadFileName), path);
        } else {
            this.cd(dir);
            channelSftp.get(FilenameUtils.separatorsToUnix(downloadFileName), FilenameUtils.separatorsToUnix(path));
        }
    }

    private void downloadDirectory(String remotePath, String localPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> childs = channelSftp.ls(remotePath);
        for (ChannelSftp.LsEntry child : childs) {
            if (child.getAttrs().isDir()) {
                if (CURRENT_DIR.equals(child.getFilename()) || PARENT_DIR.equals(child.getFilename())) {
                    continue;
                }
                new File(FilenameUtils.separatorsToUnix(localPath + File.separator + child.getFilename())).mkdirs();
                downloadDirectory(FilenameUtils.separatorsToUnix(remotePath + File.separator + child.getFilename() + File.separator),
                        FilenameUtils.separatorsToUnix(localPath + File.separator + child.getFilename()));
            } else {
                channelSftp.get(FilenameUtils.separatorsToUnix(remotePath + File.separator + child.getFilename()),
                        FilenameUtils.separatorsToUnix(localPath + File.separator + child.getFilename()));
            }
        }
    }

    public boolean isDirectory(String remotePath) throws SftpException {
        Vector<ChannelSftp.LsEntry> childs = channelSftp.ls(remotePath);
        return childs.size() != 1;
    }

    public void disconnection() {
        channelSftp.quit();
    }
}
