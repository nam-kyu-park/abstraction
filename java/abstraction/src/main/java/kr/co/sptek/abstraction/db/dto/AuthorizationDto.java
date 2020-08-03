package kr.co.sptek.abstraction.db.dto;

public class AuthorizationDto {

    String company 		= "";		// Company ID (UUID)
    String mdc 		    = "";		// MDC ID (UUID)
    String code 		= "";		// Activate Code (UUID)
    String startDate 	= "";		// 시작일시
    String endDate 		= "";		// 종료일시

    /**
     * @return the Company ID (UUID)
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the Company ID to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the MDC ID (UUID)
     */
    public String getMdc() {
        return mdc;
    }

    /**
     * @param mdc the MDC ID to set
     */
    public void setMdc(String mdc) {
        this.mdc = mdc;
    }

    /**
     * @return the Activate Code (UUID)
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the Activate Code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the Start Date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the Start Date to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the End Date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the End Date to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AuthorizationDto: [" +
                "company=" + company +
                ", mdc=" + mdc +
                ", code=" + code +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                "]";

    }
}