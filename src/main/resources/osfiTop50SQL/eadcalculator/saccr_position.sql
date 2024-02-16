select p.Parnt_Trade_ID from SA_CCR.POSITN p
where p.Bus_dt=TO_DATE(?, 'YYYY-MM-DD')
and p.PRODUCT_ID in ('72','71')
and p.RUN_ID in (select max(run_id) from SA_CCR.POSITN where bus_dt=TO_DATE(?, 'YYYY-MM-DD') and SRC_SYS_CD='GFI')