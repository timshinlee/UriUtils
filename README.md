# UriUtils
从Uri获取文件所在路径的工具类，整理自StackOverflow及一些示例代码。<br/>
如果使用真实路径去读取文件,6.0开始拥有READ_EXTERNAL_STORAGE权限才能正常读取。如果直接使用返回的Uri去读取就不需要。

无法解析自定义FileProvider生成的Uri，因为使用ContentResolver查询该类Uri只能查到_display_name和_size两个字段。
# import
build.gradle of the overall project:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
build.gradle of the module:
```
dependencies {
        compile 'com.github.timshinlee:UriUtils:1.0.1'
}
```
