@echo off


SET cpath=%CLASSPATH%

@echo off

For %%i in (%0) Do Set CP=%%~dpi
Set CP=%CP:~0,-1%

set BINFOLDER=%CP%

set LP=%BINFOLDER%

:LOOP
If "%LP:~-1,1%"=="\" GoTo :DONE
Set LP=%LP:~0,-1%
GoTo :LOOP
:DONE
@echo on
cd %BINFOLDER%
del "%BINFOLDER%\debug.txt" /F /Q


Set JAR1=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/activation-1.1.jar;
Set JAR2=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/btf-1.2.jar;
Set JAR3=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/guava-16.0.1.jar;

Set JAR6=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/hamcrest-core-1.3.jar;
Set JAR7=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jackson-annotations-2.7.0.jar;
Set JAR8=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jackson-core-2.7.3.jar;
Set JAR9=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jackson-coreutils-1.8.jar;

Set JAR10=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jackson-databind-2.7.3.jar;
Set JAR11=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/joda-time-2.3.jar;
Set JAR12=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jopt-simple-4.6.jar;     
Set JAR13=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/json-schema-core-1.2.5.jar;   
Set JAR14=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/json-schema-validator-2.2.6.jar;              
Set JAR15=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/jsr305-3.0.0.jar;                
Set JAR16=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/libphonenumber-6.2.jar;               
SET JAR17=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/mailapi-1.4.3.jar;
SET JAR18=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/msg-simple-1.1.jar;
SET JAR19=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/rhino-1.7R4.jar;          
SET JAR20=%BINFOLDER%/lib/json-schema-validator-3.0.5-deps/uri-template-0.9.jar;
SET JAR21=%BINFOLDER%/lib/rest-assured-3.0.5-deps/commons-codec-1.9.jar;
SET JAR22=%BINFOLDER%/lib/rest-assured-3.0.5-deps/commons-lang3-3.4.jar;
Set JAR23=%BINFOLDER%/lib/rest-assured-3.0.5-deps/commons-logging-1.2.jar;
Set Jar24=%BINFOLDER%/lib/rest-assured-3.0.5-deps/groovy-2.4.9.jar;
SET JAR25=%BINFOLDER%/lib/rest-assured-3.0.5-deps/groovy-json-2.4.9.jar;
SET JAR26=%BINFOLDER%/lib/rest-assured-3.0.5-deps/groovy-xml-2.4.9.jar;
SET JAR27=%BINFOLDER%/lib/rest-assured-3.0.5-deps/hamcrest-core-1.3.jar;
SET JAR28=%BINFOLDER%/lib/rest-assured-3.0.5-deps/hamcrest-library-1.3.jar;
SET JAR29=%BINFOLDER%/lib/rest-assured-3.0.5-deps/httpclient-4.5.3.jar;
SET JAR30=%BINFOLDER%/lib/rest-assured-3.0.5-deps/httpcore-4.4.6.jar;
SET JAR31=%BINFOLDER%/lib/rest-assured-3.0.5-deps/httpmime-4.5.1.jar;
SET JAR32=%BINFOLDER%/lib/rest-assured-3.0.5-deps/tagsoup-1.2.1.jar;
SET JAR33=%BINFOLDER%/lib/json-schema-validator-3.0.5.jar;
SET JAR34=%BINFOLDER%/lib/rest-assured-3.0.5.jar;
SET JAR36=%BINFOLDER%/lib/log4j-1.2.15.jar;
Set JAR37=%BINFOLDER%/lib/JSTAF.jar;
Set JAR39=%BINFOLDER%/lib/Extent-Report-2.41/extentreports-2.41.2.jar;
Set JAR40=%BINFOLDER%/lib/Extent-Report-2.41/freemarker-2.3.23.jar;
SET JAR41=%BINFOLDER%/lib/guice-4.1.0.jar;
SET JAR42=%BINFOLDER%/lib/reportng-1.1.4.jar;
SET JAR43=%BINFOLDER%/lib/velocity-dep-1.5.jar;
SET JAR44=%BINFOLDER%/lib/testng.jar;
SET JAR45=%BINFOLDER%/lib/json-lib-2.4-jdk15.jar;
SET JAR46=%BINFOLDER%/lib/java-json.jar;
SET JAR47=%BINFOLDER%/lib/commons-io-2.5.jar;
SET JAR48=%BINFOLDER%/lib/smtp.jar;

SET CLASSPATH=%CLASSPATH%;%JAR2%;%JAR3%;%JAR6%;%JAR7%;%JAR8%;%JAR9%;%JAR10%;%JAR11%;%JAR12%;%JAR13%;%JAR14%;%JAR15%;%JAR16%;%JAR17%;%JAR18%;%JAR19%;%JAR20%;%JAR21%;%JAR22%;%JAR23%;%JAR24%;%JAR25%;%JAR26%;%JAR27%;%JAR28%;%JAR29%;%JAR30%;%JAR31%;%JAR32%;%JAR33%;%JAR34%;%JAR35%;%JAR36%;%JAR37%;%JAR39%;%JAR40%;%JAR41%;%JAR42%;%JAR43%;%JAR44%;%JAR45%;%JAR46%;%JAR47%;%JAR48%;
SET CLASSPATH=%CLASSPATH%;
SET CLASSPATH=%CLASSPATH%;%BINFOLDER%\bin;
set CLASSPATH=.;%CLASSPATH%

@echo on


echo %CLASSPATH%

cd %BINFOLDER%
java org.testng.TestNG E:\spog_auto\latest\CCCH\xml\api\Rakesh.Chalmala\Vol_BQ.xml -d %BINFOLDER%\test-output