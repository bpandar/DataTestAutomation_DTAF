select
    a.CPTY_CD,

    --sum(nvl(a.CCP_VARITN_MRGN_RECVD,0.0) + nvl(a.CCP_VARITN_MRGN_POSTD,0.0)) VM_RECD,

    --Netted and NonNettedMTM
    sum(case WHEN nvl(a.NETG_SET_ID,'0') != '0' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)  else 0.0 end) NettedMTM,
    sum(case WHEN nvl(a.NETG_SET_ID,'0')  = '0' and (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) >0  THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)  else 0.0 end) NonNettedMTM_GrossCE,
    sum(case WHEN nvl(a.NETG_SET_ID,'0')  = '0' and (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) <0  THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)  else 0.0 end) NonNettedMTM_GrossPayable,

    -- Positive MTM
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='CR' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMCredit,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='CO' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMCommodities,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='EQ' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMEquity,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='FX' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMFX,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='IR' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMIR,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)>0 AND a.SACCR_ASSET_CLASS='OT' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) PosDerivativesMTMOther,

    -- Negative MTM
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='CR' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMCredit,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='CO' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMCommodities,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='EQ' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMEquity,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='FX' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMFX,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='IR' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMIR,
    sum(case WHEN a.CPTY_CD like '%_dummy' AND (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM)<0 AND a.SACCR_ASSET_CLASS='OT' THEN (NVL(a.CCP_VARITN_MRGN_POSTD,0)+a.MTM) ELSE 0.0 END) NegDerivativesMTMOther,

    -- DerivativesNotional
    sum(case WHEN a.SACCR_ASSET_CLASS='CR' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalCredit,
    sum(case WHEN a.SACCR_ASSET_CLASS='CO' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalCommodities,
    sum(case WHEN a.SACCR_ASSET_CLASS='EQ' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalMTMEquity,
    sum(case WHEN a.SACCR_ASSET_CLASS='FX' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalFX,
    sum(case WHEN a.SACCR_ASSET_CLASS='IR' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalIR,
    sum(case WHEN a.SACCR_ASSET_CLASS='OT' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalOther,
    sum(case WHEN a.EXPSR_TYPE='OTCDRV' THEN a.BSL_NOTIONAL ELSE 0.0 END) DerNotionalTotalOTC,

    a.VARITN_MRGN_CCY_CD CCY_CD
from SA_CCR.RPT_CCBASEL_REPORT a
where a.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
and a.RUN_ID = (
    select max(b.RUN_ID)
    from SA_CCR.RPT_CCBASEL_REPORT  b
    where b.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
)
and a.CCP_IND = 'Y'
and src_sys_cd not in ('GFI_CM')
Group by a.CPTY_CD, a.VARITN_MRGN_CCY_CD