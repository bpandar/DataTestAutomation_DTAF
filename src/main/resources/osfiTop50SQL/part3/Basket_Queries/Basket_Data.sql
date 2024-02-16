WITH
	T AS (
select p.run_id,p.bus_dt,
(CASE
		WHEN UPPER(p.PAYER_RECVR)='PAYER' THEN 'S'
		WHEN UPPER(p.PAYER_RECVR)='RECEIVER' THEN 'B'
		WHEN UPPER(p.BUY_SELL)='SELL' THEN 'S'
		WHEN UPPER(p.BUY_SELL)='BUY' THEN 'B'
	ELSE ''
	END) AS buy_sell
, p.src_sys_cd, 
(CASE 
		WHEN p.PARNT_TRADE_ID='<NA>' THEN p.TRADE_ID
		ELSE p.PARNT_TRADE_ID
	END) AS KEY_ID, p.trade_id,
p.sec_symbol,b.BSKT_NAME,b.UNDRLY_SEC_SYMBOL,b.BSKT_CCY_CD,b.WT as MKT_NOTIONAL
from sa_ccr.positn p,sa_ccr.mkt_eq_bskt b where p.bus_dt = to_date(?,'yyyy-mm-dd') and p.bus_dt = b.bus_dt
and p.expr_run_id = '99999999' and p.sec_symbol = b.bskt_name
)
Select 
T.run_id,T.bus_dt,
T.buy_sell
, T.src_sys_cd, T.trade_id,T.sec_symbol,T.BSKT_NAME,T.UNDRLY_SEC_SYMBOL, T.BSKT_CCY_CD,T.MKT_NOTIONAL
from T
LEFT JOIN SA_CCR.ST_CCBASEL_TRADE BSL ON T.KEY_ID=BSL.CCR_TRADE_ID AND T.SRC_SYS_CD=BSL.CCR_SRC_SYS_CD AND T.BUS_DT=BSL.BUS_DT AND BSL.EXPR_RUN_ID=99999999
LEFT JOIN SA_CCR.RPT_CCBASEL_REPORT RPT ON BSL.TRADE_ID = RPT.TRADE_ID AND BSL.SRC_SYS_CD=RPT.SRC_SYS_CD AND BSL.BUS_DT=RPT.BUS_DT
WHERE 
	RPT.RUN_ID in (select max(run_id)from sa_ccr.rpt_ccbasel_report where bus_dt=TO_DATE(?, 'YYYY-MM-DD'))
  AND RPT.INTRNL_EXTR_IND='N'