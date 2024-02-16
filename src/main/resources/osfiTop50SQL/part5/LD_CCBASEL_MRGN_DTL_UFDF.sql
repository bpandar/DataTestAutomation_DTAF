select
    sum(UNFUNDED_DEFAULT_FUND) EXPSR_AMT,
    CPTY_CD,
    UNFUNDED_DEFAULT_FUND_CCY CCY_CD
from SA_CCR.RF_CCP_MARGIN
where START_DT <= TO_DATE(?, 'YYYY-MM-DD')
and END_DT > TO_DATE(?, 'YYYY-MM-DD')
and DERIV_MRGN_IND = 'Y'
and UNFUNDED_DEFAULT_FUND_CCY is not null
GROUP BY CPTY_CD, UNFUNDED_DEFAULT_FUND_CCY
