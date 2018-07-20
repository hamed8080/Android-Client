Android Client is Powerfull client for request and download file in android with ultra simple builder design pattern .
this libs can be attached to android applications as moudle .
some of feature is :
1- jwt authorization
2- download manager
3- string downloader for download file like image base64 encoded
<h3>Usage</h3>
1- download aar file and add it like a moudle  from new->moudle->add jar or aar<br/>
2-  check gradle of app level have this line

     implementation project(path: ':WebService')
3- add jackson dependency for json serilizer like below
    
     implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.4
     implementation 'com.fasterxml.jackson.core:jackson-core:2.9.4'
     implementation 'com.fasterxml.jackson.core:jackson-annotations:2.9.4'

