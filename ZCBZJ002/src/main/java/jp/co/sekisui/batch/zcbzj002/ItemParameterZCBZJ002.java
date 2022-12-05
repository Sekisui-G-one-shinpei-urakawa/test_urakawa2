package jp.co.sekisui.batch.zcbzj002;

import org.springframework.stereotype.Component;

import jp.co.sekisui.batch.base.ItemParameterBase;
import lombok.Getter;
import lombok.Setter;

/**
 * パラメータクラス
 * （業務実装）
 */
//@Data
@Getter
@Setter
@Component
public class ItemParameterZCBZJ002 extends ItemParameterBase {
    private String company_code; // 会社コード
    private String document_type[]; // 伝票タイプ
    private String financialaccount_type; // 勘定タイプ
    private String calendar_id; // カレンダーID
    private String check_amount; // 支払方法判定額
    private String bills; // 支払方法種別：手形
    private String bank_transfer; // 支払方法種別：銀行振込
    private String payment_method; // 支払方法判定種別
}