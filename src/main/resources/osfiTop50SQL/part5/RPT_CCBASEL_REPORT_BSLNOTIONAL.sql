select
a.CPTY_CD,
a.BSL_NOTIONAL,
a.SACCR_ASSET_CLASS,
a.VARITN_MRGN_CCY_CD CCY_CD
from SA_CCR.RPT_CCBASEL_REPORT a
where a.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
and a.RUN_ID = (
    select max(b.RUN_ID)
    from SA_CCR.RPT_CCBASEL_REPORT  b
    where b.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
)