# OpenGL_ES_Learning
OpenGl es 学习记录
从去年以来就萌生了学习openGl的想法，学了一点点，后面又由于其它一些琐事，放下了。今年疫情，公司倒闭，也体验了一把艰难讨薪过程。现在尘埃初定，
想努力往音视频方向深入学习一下，而OepnGl ES是一门必须要掌握的技术。此次将以吴亚峰老师编撰的《OpenGl ES 3.X游戏开发》作为入门教程进行学习总结。

## 一、一个不算简单的OpenGl ES 小Demo
Dmeo 是画一个绕x轴不断旋转的三角形，刚开始别问为什么，直接照着撸了一遍代码，然后再自己慢慢分析，大概了解每一行代码的意思，掌握大体步骤。
具体可看triangle包下的代码，我将简单总结一个具体的流程和步骤。

1. 创建一个Activity,不再setContentView了，而是将自定义的GLSurfaceView设置进去，后续就是要这个view中进行绘制；

   GLSurfaceView 是继承于SurfaceView的，此类官方解释是：An implementation of SurfaceView that uses the dedicated surface for
 displaying OpenGL rendering. 
2. 在自定义的GLSurfaceView构造函数中进行一些初始化的操作；
  * 指定OpenGl ES 使用的版本；
  * 创建并设置渲染器 GLSurfaceView.Renderer，图形主要是通过渲染器的一系列操作渲染出来的，这个很关键；
  * 设置渲染模式，因为要三角形不断绕x轴旋转，所以模式是 GLSurfaceView.RENDERMODE_CONTINUOUSLY；
3. 自定义Renderer 实现GLSurfaceView.Renderer 接口，需要重写三个方法，渲染过程主要通过这三个方法来完成。
  * onSurfaceCreated ：GLSurfaceView创建好的时候调用，这里要做以下几个操作：
  
    * 设置清屏色为黑色，GLES30.glClearColor(）方法；
    * 创建三角形对象，对象内主要进行顶点数据的处理；
    * 打开深度检测，其实这一步无所谓，去掉也行；
    * 开启一个线程，不断改变三角形绕x轴旋转的角度；
  * onSurfaceChanged ：GLSurfaceView大小改变的时候调用，执行步骤有
  
    * 设置视口，即我们能看到画面的区域，GLES30.glViewport(0,0,width,height);
    * 设置投影矩阵， Matrix.frustumM(Triangle.projectMatrix,0,-ratio,ratio,-1,1,1,10); 具体参数解释如下：
      注意点：ratio 为宽高比，宽设置为ratio则高设置为1，这样能保证视图窗口不变形；近、远平面视图之间就是视景体，投影在这之间的物体才能被看到；
      远平面值要设置比近平面大，而且两者差值不能过小，防止绘制东西物体太大视图显示不全；
      ```
         * @param m the float array that holds the output perspective matrix
         * @param offset the offset into float array m where the perspective matrix data is written
         * @param left
         * @param right
         * @param bottom
         * @param top
         * @param near 近平面
         * @param far  远平面
         public static void frustumM(float[] m, int offset,
            float left, float right, float bottom, float top,
            float near, float far) {}
      ```
     * 设置摄像机位置，Matrix.setLookAtM(Triangle.vMatrix,0,0,0,3,0,0,0,0,1,0);参数解释如下：
        注意点：eyeZ的位置，即眼球位置需要大于近平面值，这样才能保证视景体内物体能被看到；
        upX,upY,upZ表示的是眼球的方向向量，upY为1,表示是正视前面，upX为1，则向右旋转了90度看物体，upZ为1则向后躺着看上面，就看不到物体了；
        这里要明白OpenGl ES 使用的是左手坐标系，即向上为Y轴正方向，向右为X轴正方向，指向自己为Z轴正方向，结合坐标系，就好理解了。
       ```
        /**
         * Defines a viewing transformation in terms of an eye point, a center of
         * view, and an up vector.
         *
         * @param rm returns the result
         * @param rmOffset index into rm where the result matrix starts
         * @param eyeX eye point X
         * @param eyeY eye point Y
         * @param eyeZ eye point Z
         * @param centerX center of view X
         * @param centerY center of view Y
         * @param centerZ center of view Z
         * @param upX up vector X
         * @param upY up vector Y
         * @param upZ up vector Z
         */
        public static void setLookAtM(float[] rm, int rmOffset,
                float eyeX, float eyeY, float eyeZ,
                float centerX, float centerY, float centerZ, float upX, float upY,
                float upZ) {}
       ```
   * onDrawFrame：绘制时调用，里面做了两个操作；
      * 清除颜色缓存和深度缓存， GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        
        为什么要清除，因为图形并不是一块显示到屏幕上的，而是在帧缓存区中渲染好，再显示的，渲染好一帧要清除一下缓存才再继续渲染绘制；
        
      * 调用三角形对象的绘制方法开始绘制；

4. 三角形渲染数据处理，主要封装在Triangle类中，里面做了三个操作：
   * 初始化三角形顶点数据（构造函数中）
      * 定义顶点位置数组，三个顶点，每个顶点由x,y,z 坐标组成，坐标的范围一般在-1，1 之间，因为我们视口的大小就在这个范围，超出的话会被裁剪，所以会这个数组大小会为9。
      三个顶点按逆时针的顺序排列；
      * 分配顶点缓冲区大小， ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4); 因为是浮点数，占4个字节，所以要乘以4；
      然后调用 vbb.order(ByteOrder.nativeOrder()); 进行字节排序；
      * 然后将缓存区转换成浮点缓冲区，然后再把定义的浮点数组put进去，然后再定义读取位置为0;
      * 再定义顶点颜色数组，流程类型顶点位置数组，颜色为rgba。然后分配顶点颜色缓冲区，转换成浮点缓冲区，再将数组put进去，定义读取位置为0;
   * 初始化着色器程序（构造函数中）（有固定的步骤，ShaderUtil工具类中封装了，详细的可以看代码，注释都标注的很清楚）
      * 加载顶点着色器脚本内容，用着色器语言编写的顶点着色器脚本，顶点数据会在这里进行一些计算、转换；
      * 加载片元着色器脚本的内容，同样用着色器语言编写的，接收从顶点着色器中处理过来的数据，进行进一步渲染处理；
      * 创建顶点着色器和片元着色器，然后创建着色器程序，将两者链接起来，就像两个管道一样，把它们接在一起，才能使用；
      * 然后获取顶点相关属性的引用，这些引用后续要传入到渲染管理中的，aPosition，aColor 是定义在顶点颜色器的属性值，uMVPMatrix，是一致性属性，即每个顶点都具有的

      ```
         //获取顶点位置属性引用id
         aPositionHandle = GLES30.glGetAttribLocation(program,"aPosition");
         //获取顶点颜色属性引用id
         aColorHandle = GLES30.glGetAttribLocation(program,"aColor");
         //获取程序总变换矩阵引用id
         uMVPMatrixHandle = GLES30.glGetUniformLocation(program,"uMVPMatrix");

      ```
   * 开始绘制，类中定义的绘制自身的方法
      * 启用着色器程序，上面创建的着色器程序，通过  GLES30.glUseProgram(program); 启用
      * 初始化旋转矩阵，然后往z轴正方向平移1个单位(当然不平移也行)，然后再设置旋转矩阵，绕x轴旋转；

      ```
         //初始化旋转变化矩阵，x,y,z必须要有一个为1，都为0,则矩阵相关变化但为0，这个理解清楚；
         Matrix.setRotateM(mMatrix,0,0,0,0,1);
         //设置沿着z轴正向平移，让视图显的更大一些
         Matrix.translateM(mMatrix,0,0,0,1);
         //设置绕x轴旋转，与setRotateM的区别就在于，rotateM 是旋转矩阵不断与角度变化的过程。
         Matrix.rotateM(mMatrix,0,xAngle,1,0,0);

      ```
      * 将变化矩阵、顶点位置数据、顶点着色数据传入到渲染管线(这一块相对难理解)

       getFinalMatrix(mMatrix)，获取最终变化矩阵。先将vMatrix(摄像机位置矩阵)与上一步我们初始化的旋转矩阵相乘，然后再将投影矩阵与上一次的乘积再相乘

       ```
            public static float[] getFinalMatrix(float[] spec){
                mVPMatrix = new float[16];
                //摄像机位置矩阵与传入矩阵相乘
                Matrix.multiplyMM(mVPMatrix,0,vMatrix,0,spec,0);
                //投影矩阵与上一步变化矩阵相乘并赋值到总变化矩阵mVPMatrix
                Matrix.multiplyMM(mVPMatrix,0,projectMatrix,0,mVPMatrix,0);
                return mVPMatrix;
            }
       ```

      ```
         //将变化矩阵传入渲染管线,
         GLES30.glUniformMatrix4fv(uMVPMatrixHandle,1,false,getFinalMatrix(mMatrix),0);
         //将顶点位置数据传入渲染管线（以浮点形式从顶点位置缓冲区中读取一次读取3位，跨度是 3*4）
         GLES30.glVertexAttribPointer(aPositionHandle,3,GLES30.GL_FLOAT,false,3*4,vertexBuffer);
         //将顶点颜色数据传入渲染管线（以浮点形式从打点颜色缓冲区中读取，一次读取4位，跨度是 4*4）
         GLES30.glVertexAttribPointer(aColorHandle,4, GLES30.GL_FLOAT,false,4 * 4,colorBuffer);

      ```

      * 启用顶点位置与颜色数据

      ```
        //启用顶点位置数据
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        //启用顶点着色数据
        GLES30.glEnableVertexAttribArray(aColorHandle);
      ```

      * 最后，开始绘制三角形

      ```
         //绘制三角形，从0开始，绘制三个顶点
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,vCount);
      ```
5.最后编译，就能在屏幕上看到不断旋转的三角形了。

总结，跟以往面前对象编程不一样，这是面向过程的，必须一步一步来绘制，当中的难度有两个：
    * 视景体与摄像机位置
    * 矩阵变化
    * 着色器编写



