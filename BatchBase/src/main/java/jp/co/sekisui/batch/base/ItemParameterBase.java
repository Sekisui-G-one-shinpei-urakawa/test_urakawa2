package jp.co.sekisui.batch.base;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * パラメータクラス
 * （スケルトン実装）
 */
@Data
public class ItemParameterBase {
    private String lang;    // 言語
    private String uuid;    // UUID
    private String program_id; // プログラムID
    private String user_id; // ユーザID
    private String variant_id; // バリアントID
    private String onbatch; // オンバッチ起動
    private String queuingstatus; // キューステータス
    private LocalDateTime job_date; // ジョブ日付
    //private int recCount;   // 同時処理レコード数
    private String destination; // Destination
    private int inCount; // 入力件数
    private int outCount; // 出力件数
    private int inErrorCount; // 入力エラー件数
    private int outErrorCount; // 出力エラー件数
    private int warnCount; // 警告件数
}