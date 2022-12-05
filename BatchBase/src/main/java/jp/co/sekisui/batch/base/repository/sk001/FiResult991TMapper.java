package jp.co.sekisui.batch.base.repository.sk001;

import java.util.List;
import jp.co.sekisui.batch.base.entity.sk001.FiResult991T;
import jp.co.sekisui.batch.base.entity.sk001.FiResult991TExample;
import org.apache.ibatis.annotations.Param;

public interface FiResult991TMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    long countByExample(FiResult991TExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    int deleteByExample(FiResult991TExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    int insert(FiResult991T record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    int insertSelective(FiResult991T record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    List<FiResult991T> selectByExample(FiResult991TExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    int updateByExampleSelective(@Param("record") FiResult991T record, @Param("example") FiResult991TExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SK001.FI_RESULT_991_T
     *
     * @mbg.generated Mon Jan 17 05:39:08 UTC 2022
     */
    int updateByExample(@Param("record") FiResult991T record, @Param("example") FiResult991TExample example);
}