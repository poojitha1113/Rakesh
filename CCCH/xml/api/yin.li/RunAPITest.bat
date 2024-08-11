@echo off

cd /d C:\Workspace\CCCH_Automation\

SET cpath=%CLASSPATH%

@echo off

For %%i in (%0) Do Set CP=%%~dpi
Set CP=%CP:~0,-1%

set BINFOLDER=%CP%

set LP=%BINFOLDER%

set BINFOLDER=C:\Workspace\CCCH_Automation\
set LP=%BINFOLDER%

echo %BINFOLDER%
echo %LP%



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
Set JAR3=%BINFOLDER%/lib/guava-23.0.jar;%BINFOLDER%/lib/jdom-2.0.6.jar;%BINFOLDER%/lib/jaxen-1.1.6.jar;

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
SET JAR43=%BINFOLDER%/lib/velocity-dep-1.5.jar;%BINFOLDER%/lib/testng.jar;%BINFOLDER%/lib/selenium-server-standalone-3.11.0.jar;


SET CLASSPATH=%CLASSPATH%;%JAR2%;%JAR3%;%JAR6%;%JAR7%;%JAR8%;%JAR9%;%JAR10%;%JAR11%;%JAR12%;%JAR13%;%JAR14%;%JAR15%;%JAR16%;%JAR17%;%JAR18%;%JAR19%;%JAR20%;%JAR21%;%JAR22%;%JAR23%;%JAR24%;%JAR25%;%JAR26%;%JAR27%;%JAR28%;%JAR29%;%JAR30%;%JAR31%;%JAR32%;%JAR33%;%JAR34%;%JAR35%;%JAR36%;%JAR37%;%JAR39%;%JAR40%;%JAR41%;%JAR42%;%JAR43%
SET CLASSPATH=%CLASSPATH%;
SET CLASSPATH=%CLASSPATH%;%BINFOLDER%\bin;
set CLASSPATH=.;%CLASSPATH%

set myfolder=%BINFOLDER%/xml/api/yin.li
set myUIFolder=%BINFOLDER%/xml/ui/yin.li
@echo on

rd /s /q C:\Workspace\CCCH_Automation\test-output
mkdir C:\Workspace\CCCH_Automation\test-output

echo %CLASSPATH%

cd %BINFOLDER%
java org.testng.TestNG %myfolder%\createSiteTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\DeleteSiteTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetAllGroupsTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetDestinationFiltersByUserIdTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetGroupByNameTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSiteByIdTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSitesTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\UpdateDestinationFilterTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\UpdateSourceGroupTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\UpdateJobDataTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\UpdateDestinationFilterForLoggedInUserTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\AgentDownloadForCloudDirectTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\CreateCloudDirectDestinations.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetDestinationSystemFiltersTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSourceFilterForLoggedUserTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSourceSystemFilterTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSpecificSourceFilterForLoggedUserTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetOrganizationIdJobsTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\UpdateJobTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetPoliciesEnhancementForRplcatRemoteRpsTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetPoliciesOfLoggedUsersOrganizationTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myfolder%\GetSharedPoliciesTest.xml -d %BINFOLDER%\test-output
java org.testng.TestNG %myUIFolder%\UserProfileUITest.xml -d %BINFOLDER%\test-output
pause


