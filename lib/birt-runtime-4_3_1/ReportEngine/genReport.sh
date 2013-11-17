################USAGE OF REPORTRUNNER#########################
# echo "org.eclipse.birt.report.engine.impl.ReportRunner Usage:";
# echo "--mode/-m [ run | render | runrender] the default is runrender "
# echo "for runrender mode: "
# echo "" "we should add it in the end <design file> "
# echo "" "--format/-f [ HTML | PDF ] "
# echo "" "--output/-o <target file>"
# echo "" "--htmlType/-t < HTML | ReportletNoCSS >"
# echo "" "--locale /-l<locale>"
# echo "" "--parameter/-p <"parameterName=parameterValue">"
# echo "" "--file/-F <parameter file>"
# echo "" "--encoding/-e <target encoding>"
# echo " "
# echo "Locale: default is english"
# echo "parameters in command line will overide parameters in parameter file"
# echo "parameter name cant include characters such as \ ', '=', ':'"
# echo " "
# echo "For RUN mode:"
# echo "we should add it in the end<design file>"
# echo "" "--output/-o <target file>"
# echo "" "--locale /-l<locale>"
# echo "" "--parameter/-p <parameterName=parameterValue>"
# echo "" "--file/-F <parameter file>"
# echo " "
# echo "Locale: default is english"
# echo "parameters in command line will overide parameters in parameter file"
# echo "parameter name cant include characters such as \ ', '=', ':'"
# echo " "
# echo "For RENDER mode:"
# echo "" "we should add it in the end<design file>"
# echo "" "--output/-o <target file>"
# echo "" "--page/-p <pageNumber>"
# echo "" "--locale /-l<locale>"
# echo " "
# echo "Locale: default is english"
################END OF USAGE #########################
if [ "$BIRT_HOME" = "" ];

then
echo " The BIRT_HOME need be set before BirtRunner can run.";
else


java_io_tmpdir=$BIRT_HOME/ReportEngine/tmpdir
org_eclipse_datatools_workspacepath=$java_io_tmpdir/workspace_dtp
mkdir -p $org_eclipse_datatools_workspacepath
unset BIRTCLASSPATH
for i in `ls $BIRT_HOME/ReportEngine/lib/*.jar`;do export BIRTCLASSPATH=$i:$BIRTCLASSPATH;done

JAVACMD='java';
$JAVACMD -Djava.awt.headless=true -cp "$BIRTCLASSPATH" -DBIRT_HOME="$BIRT_HOME/ReportEngine" -Dorg.eclipse.datatools_workspacepath="$org_eclipse_datatools_workspacepath" org.eclipse.birt.report.engine.api.ReportRunner ${1+"$@"}

fi
