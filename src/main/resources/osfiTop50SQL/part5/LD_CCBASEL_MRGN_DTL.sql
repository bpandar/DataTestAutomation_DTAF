select
    sum(EXPSR_AMT) EXPSR_AMT,
    trim(CPTY_CD) CPTY_CD,
    trim(CCY_CD) CCY_CD
from SA_CCR1.LD_CCBASEL_MRGN
where BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
and trim(RCORD_TYPE)=?
and RUN_ID = (
    select max(b.RUN_ID)
    from SA_CCR1.LD_CCBASEL_MRGN  b
    where
        b.BUS_DT = TO_DATE(?, 'YYYY-MM-DD')
        and trim(b.RCORD_TYPE)=?
)
GROUP BY RCORD_TYPE, CPTY_CD, CCY_CD