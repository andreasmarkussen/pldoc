<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet to turn the XML output of CPD into a nice-looking XHTML page -->
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" 
	doctype-system="http://www.w3.org/TR/xtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>

<xsl:template match="/file">
<html>
	<head>
		<title>Source Code</title>
		<link href="sourcestylesheet.css" rel="stylesheet" />
		<!-- Add line numbers -->
		<style type="text/css" >
			.code { counter-reset: listing; } 
			span { counter-increment: listing; } 
			.code span:before { content: counter(listing); color: gray;  } 
		</style>
	</head>
<body class="code">

<pre class="code" >
<h2 class="TableHeadingColor" >Source Code</h2>
    <xsl:for-each select="line"><xsl:element name="a">
	<xsl:variable name="sourceLine" select="@number" />
    	<xsl:attribute name="name"><xsl:value-of select="@number"/></xsl:attribute>
	<xsl:element name="span">&#160;<xsl:value-of select="text()" /></xsl:element></xsl:element>&#160;<br/></xsl:for-each>
</pre>
</body>
</html>
</xsl:template>

</xsl:stylesheet>

