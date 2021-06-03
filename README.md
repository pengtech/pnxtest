# PnxTest
![image](/.png)
PnxTest is a Java-based test automation framework that unites all testing layers: Mobile applications, WEB applications, REST services.
Due to fluent API design and rich ecosystem applications, PnxTest will make your automation test more efficient.

**Current release version**: 1.0.1

**User guide:** https://pnxtest.com/user-guide

**Online Report Reference**: https://pnxtest.com/user-guide/demo/reporting.html

**Demo/Scaffolding Project**: https://github.com/pengtech/integrationTest

**Maven dependency:**

```xml
    <!--PnxTest starter parent-->
    <parent>
        <artifactId>pnx-test-starter-parent</artifactId>
        <groupId>com.pnxtest</groupId>
        <version>1.0.1</version>
    </parent>

    <dependencies>
        <!--PnxTest framework core-->
        <dependency>
            <groupId>com.pnxtest</groupId>
            <artifactId>pnx-test-starter-core</artifactId>
        </dependency>
        <!--optional, add this if you need to test http api-->
        <dependency>
            <groupId>com.pnxtest</groupId>
            <artifactId>pnx-test-starter-http</artifactId>
        </dependency>
        <!--optional, add this if you need to operate and validate database-->
        <dependency>
            <groupId>com.pnxtest</groupId>
            <artifactId>pnx-test-starter-db</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>
```



