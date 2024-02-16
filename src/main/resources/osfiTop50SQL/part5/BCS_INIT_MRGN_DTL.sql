select
    trim(CREDIENT_CUSTOMER_CD) CPTY_CD,
    trim(SRC_CCY) CCY_CD,
    sum(EXP_AMT) EXPSR_AMT
from BSL.BCS_INIT_MRGN_DTL
where BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
and trim(RCORD_TYPE)=?
and RUN_ID = (
    select max(b.RUN_ID)
    from BSL.BCS_INIT_MRGN_DTL  b
    where
        b.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
        and trim(b.RCORD_TYPE)=?
)
GROUP BY CPTY_CD, CCY_CD