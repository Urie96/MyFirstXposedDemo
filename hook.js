//定义一个名为hookTest1的函数
function hookTest1() {
  //获取一个名为"类名"的Java类，并将其实例赋值给JavaScript变量utils
  var utils = Java.use('me.kyuubiran.xposedapp.Guess');
  //修改"类名"的"method"方法的实现。这个新的实现会接收两个参数（a和b）
  utils.isDraw.implementation = function (a, b) {
    //将参数a和b的值改为123和456。
    a = 123;
    //调用修改过的"method"方法，并将返回值存储在`retval`变量中
    var retval = this.isDraw(a);
    //在控制台上打印参数a，b的值以及"method"方法的返回值
    console.log(a, b, retval);
    //返回"method"方法的返回值
    return true;
  };
}

function hookTest2() {
  //枚举所有的类与类的所有方法,异步枚举
  Java.enumerateLoadedClasses({
    onMatch: function (name, handle) {
      //过滤类名
      if (name.indexOf('me.kyuubiran.xposedapp') != -1) {
        console.log(name);
        var clazz = Java.use(name);
        console.log(clazz);
        var methods = clazz.class.getDeclaredMethods();
        console.log(methods);
      }
    },
    onComplete: function () {},
  });
}

function main() {
  Java.perform(function () {
    hookTest1();
    hookTest2();
  });
}
setImmediate(main);