package master_jun.Util;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OkHttpClientUtil {

	private OkHttpClient client;

	/* 종목조회 
	 * 	market	업비트에서 제공중인 시장 정보	String
		korean_name	거래 대상 암호화폐 한글명	String
		english_name	거래 대상 암호화폐 영문명	String
		market_warning	유의 종목 여부
		NONE (해당 사항 없음), CAUTION(투자유의)	String
	 * */
	public String getMarketCd() {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.upbit.com/v1/market/all?isDetails=false")
		  .get()
		  .addHeader("Accept", "application/json")
		  .build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	/* 캔들조회 분봉
	 * 	market	마켓명	String
		candle_date_time_utc	캔들 기준 시각(UTC 기준)	String
		candle_date_time_kst	캔들 기준 시각(KST 기준)	String
		opening_price	시가	Double
		high_price	고가	Double
		low_price	저가	Double
		trade_price	종가	Double
		timestamp	해당 캔들에서 마지막 틱이 저장된 시각	Long
		candle_acc_trade_price	누적 거래 금액	Double
		candle_acc_trade_volume	누적 거래량	Double
		unit	분 단위(유닛)	Integer
	 * */
	public String getCandleMin(String CandleType, String maketCd, String cnt) {

		Request request = new Request.Builder().url("https://api.upbit.com/v1/candles/minutes/"+CandleType+"?market="+maketCd+"&count="+cnt)
				.get().addHeader("Accept", "application/json").build();
		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	
	/*
	 * 캔들조회 일봉
	market	마켓명	String
	candle_date_time_utc	캔들 기준 시각(UTC 기준)	String
	candle_date_time_kst	캔들 기준 시각(KST 기준)	String
	opening_price	시가	Double
	high_price	고가	Double
	low_price	저가	Double
	trade_price	종가	Double
	timestamp	마지막 틱이 저장된 시각	Long
	candle_acc_trade_price	누적 거래 금액	Double
	candle_acc_trade_volume	누적 거래량	Double
	prev_closing_price	전일 종가(UTC 0시 기준)	Double
	change_price	전일 종가 대비 변화 금액	Double
	change_rate	전일 종가 대비 변화량	Double
	converted_trade_price	종가 환산 화폐 단위로 환산된 가격(요청에 convertingPriceUnit 파라미터 없을 시 해당 필드 포함되지 않음.)	Double
	*/
	public String getCandleDay(String CandleType, String maketCd, String cnt) {

		Request request = new Request.Builder().url("https://api.upbit.com/v1/candles/days/"+CandleType+"?market="+maketCd+"&count="+cnt)
				.get().addHeader("Accept", "application/json").build();
		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	/* 체결조회 */
	public String getTradesTicks() {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.upbit.com/v1/trades/ticks?count=1")
		  .get()
		  .addHeader("Accept", "application/json")
		  .build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	/* 현재가 정보 */
	public String getTicker() {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.upbit.com/v1/ticker")
		  .get()
		  .addHeader("Accept", "application/json")
		  .build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/* 호가정보 */
	public String getOrderbook() {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.upbit.com/v1/orderbook")
		  .get()
		  .addHeader("Accept", "application/json")
		  .build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
