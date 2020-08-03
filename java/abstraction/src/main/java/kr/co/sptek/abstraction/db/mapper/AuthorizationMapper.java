package kr.co.sptek.abstraction.db.mapper;

import kr.co.sptek.abstraction.db.dto.AuthorizationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthorizationMapper {

    // Return agent expiry date
    AuthorizationDto selectExpiryDate(
            @Param("company") String company,
            @Param("mdc") String mdc,
            @Param("code") String code
    ) throws Exception;

    // Insert agent expiry date
    void insertExpiryDate(
            @Param("company") String company,
            @Param("mdc") String mdc,
            @Param("code") String code,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    ) throws Exception;

}
