 base层package说明：
 1、bsae：存放activity/fragment等基础类

 2、compression：图片压缩功能：
               使用：PictureCompression类名直接调用方法

 3、takingPhoto：拍照/从相册选择图片并裁剪 （一般在头像功能使用）
                使用：PictureHelper类名直接调用方法打开相应的获取图片方式

 4、lottie 加载json动画的包 来自于 https://github.com/airbnb/lottie-android
            使用 <com.jushi.library.lottie.LottieAnimationView
                           android:id="@+id/LottieAnimationView"
                           android:layout_width="match_parent"
                           android:layout_height="match_parent"
                           app:lottie_autoPlay="true"
                           app:lottie_fileName="animation_1.json"
                           app:lottie_loop="true" />
