<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" indent="yes" doctype-system="about:legacy-compat" />

  <xsl:template match="/">
    <html>
      <head>
	<style type="text/css">
	  div {width:50px;height:60px;}
	  a div span { border: 1px solid black;visibility:hidden;padding: 3px;text-align:center;background-color:white;color:black;position:absolute;z-index:1;display: inline-block;width:auto;white-space:nowrap;}
	  a div:hover span { visibility: visible;}
	  .divMain {display:block;position:fixed;left:0;top:0;width:100%;height:100%;overflow:auto;}
	</style>
      </head>
      <body>
	<div class="divMain">
	  <img src="Tasks.png"/>
	  <span style="position:absolute;display:block;left:0;top:0;width:100%;">
	    <xsl:apply-templates select="*"/>
	  </span>
	</div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="*">
    <a target="_blank">
      <xsl:if test="@ExternalLinkURL != ''">
	<xsl:attribute name="href">
	  <xsl:value-of select="@ExternalLinkURL"/>
	</xsl:attribute>
      </xsl:if>
      <div id="{@Name}{@ID}" style="z-index:1;position:absolute;left:{@XPos}px;width:50px;height:60px;top:{@YPos}px;">
	<xsl:if test="@ExternalLinkURL != ''">
	  <xsl:attribute name="style">
	    <xsl:value-of select="concat('z-index:1;position:absolute;left:', @XPos, 'px;width:50px;height:60px;top:', @YPos, 'px;cursor:pointer;')"/>
	  </xsl:attribute>
	</xsl:if>
	<span><xsl:value-of select="@Notes"/></span>
      </div>
    </a>
    <xsl:apply-templates select="*"/>
  </xsl:template>
</xsl:stylesheet>
