# OpenGL_ES_Learning
OpenGl es 学习记录
从去年以来就萌生了学习openGl的想法，学了一点点，后面又由于其它一些琐事，放下了。今年疫情，公司倒闭，也体验了一把艰难讨薪过程。现在尘埃初定，
想努力往音视频方向深入学习一下，而OepnGl ES是一门必须要掌握的技术。此次将以吴亚峰老师编撰的《OpenGl ES 3.X游戏开发》作为入门教程进行学习总结。

## 一、一个不算简单的OpenGl ES 小Demo
Demo 是画一个绕x轴不断旋转的三角形，刚开始别问为什么，直接照着撸了一遍代码，然后再自己慢慢分析，大概了解每一行代码的意思，掌握大体步骤。
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

总结，跟以往面前对象编程不一样，这是面向过程的，必须一步一步来绘制，当中的难度有三个：

 * 视景体与摄像机位置
 * 矩阵变化
 * 着色器编写

## 二、OpenGl着色器与渲染管线

   了解OpenGl ES 3.0 的可编程渲染管线，非常有助于从全局上理解它的运作过程，便于后续的编程学习。图片展示不出来的话，到目录下pictures文件夹中查看即可。
   
   ![](/pictures/openglrenderpiple.jpg)


## 三、OpenGl ES着色器语言
   
  可以结合上个demo中顶点着色器源码与片元着色器源码来理解，这门语言：
  
  顶点着色器源码：
  
  ```
    #version 300 es                //版本是3.0
    uniform mat4 uMVPMatrix;     //总变化矩阵
    layout (location = 0) in vec3 aPosition;        //顶点位置
    layout (location = 1) in vec4 aColor;           //顶点颜色
    out vec4 vColor;                                //传递给片元着色器的out变量
    void main() {
        gl_Position = uMVPMatrix * vec4(aPosition,1);    //根据矩阵变化计算绘制顶点的位置
        vColor = aColor;                                //将接收的颜色传递给片元着色器
    }

  ```
  片元着色器源码：
  
  ```
    #version 300 es                 //版本是3.0
    precision mediump float;        //指定全局浮点精度
    in vec4 vColor;                 //接收从顶点着色器传递过来的参数
    out vec4 fragColor;             //输出的片元颜色
    
    void main() {
        fragColor = vColor;         //给此片元赋值
    }

  ```
    
### 数据类型
  着色器语言支持以下这些数据类型：
  1. 标量：bool、int(有符号整形)、uint(无符号整型) 与float;
  2. 向量：vec2(2个浮点数的向量),ivec2(2个整数的向量),bvec2(2个布尔数的向量),uvec2(2个无符号整数的向量)
  3. 矩阵：mat3x4 (3列4行矩阵)，opengl中矩阵一般看作几个列向量组成。
  4. 采样器：sampler2D、sampler3D等，专门用来进行纹理采样的，不能在着色器中进行初始化，一般情况下用uniform限定符修饰；
  5. 结构体，使用stuct 关键字进行修饰，如下：
  
  ```
    struct info {
        vec3 color;
        vec3 position;
        vec2 textureCoor;
    }
  ```
    
   6.数组；
   7.空类型：void

### 存储限定符
    
   限定符 | 说明
   ------|--------
   const | 声明常量，只读、值初始化赋值后不可变，不能修饰结构体内的成员变量，但可修饰结构体。
   in/centroid in | 输入变量，是从宿主程序中传递过来的；顶点着色器中只能用in修饰全局变量，修饰数据类型只能是标量、向量和矩阵；片元着色器可用in/centroid in 修饰全局变量，修饰数据类型有标量、向量、矩阵、数组、结构体 
   uniform | 一致变量限定符，也是从宿主程序中传入的，可在顶点与片元着色器中使用，并可支持所有的基本数据类型；
   out/centroid out | 输出变量；顶点着色器中都可使用，修饰数据类型只能是标量、向量、矩阵、数组、结构体；片元着色器中只能使用out,只能修饰标量、向量、数组。
   
### 插值限定符

   限定符 | 说明
   ------|--------
   smooth | 以平滑方式插值得到片元输入变量, 默认方式
   flat | 不对变量使用插值，使用特定的值
   总结 | 限定符在 in/centroid in 、out/centroid out 之前使用，只能修饰顶点着色器中的out 变量和片元着色器的in 变量，且两者的插值限定符必须保持一致。否则可能导致片元着色器接收顶点着色器中传递过来的数值有问题。
    
### layout 限定符
   是从OpenGl ES 3.0才开始出现的，主要用于设置变量的存储索引。其修饰的变量必须是全局的。
   
   * 可以你作为接口块定义的一部分或接口块的成员，比如：
   
   ```
        layout uniform MatrixBlock {
            mat4 M1;
            layout mat4 uMVPMatrix;
            mat4 M2;
        }

   ```

   * 可以仅仅用来修饰uniform,如上；
   * 还可以用于修饰被接口限定符修饰的单独变量，接口限定符，有 in、out、uniform三种,语法如下：
   
  ```
    <layout 限定符> <接口限定符> <变量声明>
  ```
   
   限定符 | 示例 | 说明
   ------|-----|-------
   layout 输入限定符 | layout (location = 0) in vec3 aPosition | 片元着色器中不能有layout输入限定符
   layout 输出限定符 | layout (location = 1) out vec4 fragColor | 片元着色器中，layout限定符将通过location值将输出变量和指定编号的绘制缓冲绑定起来，这个输出变量的值将写入相应的缓冲中；location的值范围在[0,MAX_DRAW_BUFFERS-1],基本范围是[0,3]。顶点着色器中不允许有layout输出限定符，片元着色器中如果只有一个变量可不用location 修饰符，默认为0，多个的话，location值不能重复。
   一致块layout限定符 | 见上面接口块定义 | 一致块可以使用这个修饰，但单独一致变量的声明不能使用。 
   
### 一致块
  作用：用来声明多个一致性变量，类似于结构体的接口块；一致块数据通过缓冲对象送入渲染管线，以这种形式批量传送数据比单个传送数据效率高，基本语法如下：
  ```
    [<layout 限定符>] uniform 一致性块名称 {成员变量列表} [<实例名>]

    uniform Transform {
        float radius;
        mat4 modelViewMatrix;
    } block_Transform;

  ```
  一致性块变量的访问：
  
  情景 |说明
  ----| ------
  未声明变量实例 | 块内的成员变量与块外一样，可直接通过变量名称访问，也可以通过<一致块名称> . <成员变量名称>
  声明变量实例 | 宿主语言可通过<一致块名称> . <成员变量名称>访问，而着色器中需要通过<实例名> . <成员变量名称>访问
  
### 着色器语言中的流程控制

  OpenGl ES 有4种流程控制分式，即 if-else , switch-case ,while-do ,for循环

### 函数声明与片元着色器中浮点变量精度指定

   1. 着色器中也支持自定义函数，基本语法如下：
   
   ```
    [精度限定符] <返回类型> 函数名称([<参数序列>]) { /*函数体*/}
    
    精度限定符，有highp、mediump 、 lowp 三种
    
   ```

   参数序列中除可以指定类型外，还可以指定参数用途：
   
   用途修饰符 | 说明
   ----------|-----
   in 修饰符 | 修饰参数为输入参数，仅供函数接收外界传入的值，默认就是这种用途修饰符
   out 修饰符 | 修饰参数为输出参数，在函数体中对输出函数赋值，可以将值传递到外界调用它的变量中，且外界变量传递进来时不能初始化。
   intout 修饰符 | 修饰参数为输入输出参数
   
   注意：着色器内只能重载用户自定义的函数，不能重写或重载内建函数。而且函数必须先声明才能使用，即声明函数要在调用之前。
   
   2. 片元着色器中浮点变量精度的指定
   
   片元着色器中使用浮点型相关类型变量，必须指定其精度，指定精度的方式如下：
   
   ```
       lowp float color;
       in mediump vec2 Coord;
       highp mat4 m;
   ```
   
   如果都使用同一种精度 ，则可以全局指定：
   
   ```
      precision <精度> <类型>

   ```

### 特殊内建变量

  1.顶点着色器内建变量 
  
  类型 | 变量名 | 说明
  ----|--------|-----
  输入变量 | gl_VertexID | 记录顶点的整数索引，3.0新增，类型为"highp int"
          | gl_InstanceID | 记录采用实例绘制时当前图元对应的实例号
  输出变量 | gl_Position | 存放处理后顶点的位置数据
          | gl_PointSize | 存储顶点的大小，默认为1，只有采用了点绘制时才有效
  
  2. 片元着色器内建变量
  
   类型 | 变量名 | 说明
   ----|--------|-----
   输入变量 | gl_FragCoord | 变量含有当前片元在视口中的坐标值 x,y,z 与1/w，通过此变量可以实现与窗口位置相关的操作
           | gl_FrontFacing | 布尔型变量，其值可以判断正在处理的片元是否属于光栅化阶段生成此片元所对应图元的正面。正面为true，反之false;
           | gl_PointCoord | vec2类型变量，启用点精灵时，表示当前图元中片元的纹理坐标，范围从0.0~1.0。当图元不是一个点，或未启用点精灵，则值不确定。
   输出变量 | gl_FragDepth | 值为片元深度值，3.0开始允许赋值。
  
  
  
  

    
   
  
   
   
   
   
   




