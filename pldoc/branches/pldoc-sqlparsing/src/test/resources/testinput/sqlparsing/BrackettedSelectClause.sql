CREATE OR REPLACE PACKAGE PKG_BrackettedSelectClause AS 
CURSOR c_cursor IS 
     select id, selector from tableX
    union all
    (
     select id, selector from tableY
     );
END;
/
