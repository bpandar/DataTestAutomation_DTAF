WITH
	T1 AS (
SELECT 
	POS.POSITN_ID, POS.PARNT_TRADE_ID, POS.BUS_DT, POS.RUN_ID, POS.TRADE_ID, POS.SRC_SYS_CD,
	(CASE 
		WHEN POS.PARNT_TRADE_ID='<NA>' THEN POS.TRADE_ID
		ELSE POS.PARNT_TRADE_ID
	END) AS KEY_ID,
	(CASE 
		WHEN UPPER(POS.PAYER_RECVR)='PAYER' THEN 'S'
		WHEN UPPER(POS.PAYER_RECVR)='RECEIVER' THEN 'B'
		WHEN UPPER(POS.BUY_SELL)='SELL' THEN 'S'
		WHEN UPPER(POS.BUY_SELL)='BUY' THEN 'B'
	ELSE ''
	END) AS BUY_SELL,	
		POS.PRODUCT_ID, PD.PRODUCT, UPPER(POS.OPT_TYPE) AS OPT_TYPE, POS.NOTIONAL,POS.AMT AS UNITS, POS.CONTRACT_SIZE, 
		POS.STRIKE, POS.CCY_1_CD AS STRIKE_CCY, POS.REF_ENTITY_NAME, POS.SEC_SYMBOL,
		EQ.SEC_TYPE AS REF_TYPE, EQ.PRICE AS REF_PRICE, EQ.CCY_CD AS REF_CCY,
	PD.PRODUCT_CD,
	PD.ASSET_CLASS
FROM
	SA_CCR.POSITN POS
	LEFT JOIN SA_CCR.MKT_EQ_DATA EQ ON (POS.SEC_SYMBOL=EQ.SEC_SYMBOL AND POS.BUS_DT=EQ.BUS_DT)
	INNER JOIN SA_CCR.RF$_PRODUCT PD ON (POS.PRODUCT_ID=PD.PRODUCT_ID)
WHERE
	POS.BUS_DT=TO_DATE(?, 'YYYY-MM-DD') 
  AND POS.RUN_ID in (select max(run_id)from sa_ccr.positn where bus_dt=TO_DATE(?, 'YYYY-MM-DD') and expr_run_id='99999999' and SRC_SYS_CD='SWAPONE' )
	AND POS.SRC_SYS_CD IN ('SWAPONE')
	AND POS.PRODUCT_ID IN ('24','70','69')
)	

SELECT 
	T1.POSITN_ID, T1.BUS_DT, T1.RUN_ID, T1.TRADE_ID, T1.PARNT_TRADE_ID, T1.SRC_SYS_CD, T1.BUY_SELL,
	RPT.CPTY_CD, RPT.CPTY_LEGL_NAME, TO_CHAR(RPT.UEN) UEN, RPT.RSPNSBTY_CENTRE, RPT.TRANSIT, RPT.LEGL_ENTITY_CD,
	T1.PRODUCT_ID, T1.PRODUCT, T1.OPT_TYPE, T1.NOTIONAL, T1.UNITS,
	decode(nvl(T1.CONTRACT_SIZE,0),0,1, T1.CONTRACT_SIZE) as CONTRACT_SIZE,
	T1.STRIKE, T1.STRIKE_CCY,
	T1.REF_ENTITY_NAME, T1.SEC_SYMBOL, '' AS UNDRLY_SPOT_TKER, T1.REF_TYPE, T1.REF_PRICE, T1.REF_CCY,
	RPT.INSTM_TYPE, RPT.MTM, RPT.BSL_NOTIONAL, RPT.BSL_NOTIONAL_CCY_CD, RPT.SACCR_TRADE_NOTIONAL, RPT.BSL_ASSET_CLASS, RPT.CCP_IND,
	T1.PRODUCT_CD, T1.ASSET_CLASS, 'SWAPONE_EQ' as SOURCE
FROM 
	T1
LEFT JOIN SA_CCR.ST_CCBASEL_TRADE BSL ON T1.KEY_ID=BSL.CCR_TRADE_ID AND T1.SRC_SYS_CD=BSL.CCR_SRC_SYS_CD AND T1.BUS_DT=BSL.BUS_DT AND BSL.EXPR_RUN_ID=99999999
 JOIN SA_CCR.RPT_CCBASEL_REPORT RPT 
ON BSL.TRADE_ID = RPT.TRADE_ID AND BSL.SRC_SYS_CD=RPT.SRC_SYS_CD AND BSL.BUS_DT=RPT.BUS_DT
WHERE 
	RPT.RUN_ID in (select max(run_id)from sa_ccr.rpt_ccbasel_report where bus_dt=TO_DATE(?, 'YYYY-MM-DD'))
  AND RPT.INTRNL_EXTR_IND='N'
  AND RPT.SRC_SYS_CD='SWAPONE'