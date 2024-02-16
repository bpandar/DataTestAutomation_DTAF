select
    sum(IM_REQUIRED) EXPSR_AMT,
    CPTY_CD,
    CCY CCY_CD
from SA_CCR.RF_CCP_MARGIN
where START_DT <= TO_DATE(?, 'YYYY-MM-DD')
and END_DT > TO_DATE(?, 'YYYY-MM-DD')
and DERIV_MRGN_IND = 'Y'
and CCY is not null
GROUP BY CPTY_CD, CCY