
CREATE OR REPLACE
TYPE BODY         TY_LOCATION AS 

MEMBER FUNCTION get_latitude RETURN NUMBER
IS
BEGIN
	 RETURN latitude;
END get_latitude;


MEMBER FUNCTION get_longitude RETURN NUMBER
IS
BEGIN
	 RETURN longitude;
END get_longitude;

MEMBER FUNCTION get_datum RETURN VARCHAR2
IS
BEGIN
	 RETURN datum;
END get_datum;

MEMBER FUNCTION get_name RETURN VARCHAR2
IS
BEGIN
	 RETURN name;
END get_name;

END;
/


SHOW ERRORS 