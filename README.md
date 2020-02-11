# 文件上传 
## Spring MVC文件上传

## FileUpload组件文件上传

### Commons-FileUpload简介
Commons是Apache开发源代码组织的一个Java子项目，该项目主要涉及一些开发中常用的模块，如文件上传、命令行处理、数据库连接池等等，FileUpload就是Commons其中的一个用于处理HTTP文件上传的子项目，其主要特点：

* 使用简单：Commons-FileUpload组件可以方便地嵌入JSP文件或Java文件中，只需要编写少量的代码即可完成文件的上传功能，十分方便。
* 全程控制上传内容：使用Commons-FileUpload组件提供的对象及操作方法，可以获得上传文件的全部信息，包括文件类型、大小、名称等，方便操作、实现逻辑控制。
* 对上传文件的大小、类型进行控制：有时候需要对用户上传的文件进行控制筛选，避免服务器臃肿，基于第二点获取文件信息，编写逻辑代码进行控制。

### Commons-FileUpload组件的API

* ServletFileUpload类，主要用于解析form表单提交的数据、设置请求信息实体内容的最大允许字节数，常用方法：
  1. void setSizeMax(long sizeMax)	 设置请求信息内容的最大允许字节数
  2. List parseRequest(HttpServletRequest request)	解析form表单提交的数据，返回一个FileItem实例的集合
  3. static final boolean isMultipartContent(HttpServletRequest request)	判断请求提交的方式是否是“multipart/form-data”类型
  4. void setHeaderEncoding(String encoding)	设置转换时所使用的字符集编码

* FileItem接口，用于封装单个表单字段元素的数据，每一个表单字段都对应一个FileItem实例，在应用程序中使用的是其实现类DiskFileItem。FileItem接口提供的常用方法如下：
  1. public boolean isFormField()	判断FileItem对象所封装的数据类型，普通表单字段返回true，文件表单字段返回false
  2. public String getName()	获得文件字段中所上传的文件名，普通表单字段调用此方法返回null
  3. public String getFieldName()	返回表单字段元素的name属性值
  4. public void write(File file)	将FileItem对象中保存的文件数据内容写入到指定的文件中
  5. public String getString()	将FileItem对象中保存的主体内容以一个字符串返回，其有一个重载方法getString(String encoding)，可指定所采用的编码集
  6. public long getSize()	返回单个上传文件的字节数

* FileItemFactory接口与其实现类DiskFileItemFactory，创建ServletFileUpload实例需要依赖FileItemFactory工厂接口，DiskFileItemFactory是此接口的实现类，主要有两个常用方法：
   1. public void setSizeThreshold(int sizeThreshold)	设置内存缓冲区的大小
   2. public void setRepositoryPath(String path)	设置临时文件存放的目录

