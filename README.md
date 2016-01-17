#### 简介
本程序通过读取屏幕信息，模拟人脑推理，操纵鼠标来实现自动扫雷。由于是读屏幕，所以通过编写不同的扫雷游戏皮肤，可以在不同扫雷游戏上工作。

#### Demo
http://www.youtube.com/watch?v=7TgYbriHI-4

http://v.youku.com/v_show/id_XMzU5MTY1MzE2.html

#### 使用说明

1. 将minesweeper-assist-yyyyMMdd.rar解压到文件夹里，运行minesweeper-assist.jar ![MainPanel.png](https://minesweeper-assist.googlecode.com/svn/wiki/MainPanel.png) 
2. 选择正确的皮肤(Skin)，填写水平格子数(X grids number)、垂直格子数(Y grids number),上图所示为高级局面的参数。
3. 选择左上角格子的左上角坐标：先将左上角格子点开，再将鼠标*准确*(需要借助放大镜)移动至左上角格子的左上角(即浅灰色区域的左上角)，点击Catch left-top origin按钮。(由于此时鼠标不可能处在该按钮上，因此你需要提前选中该按钮，再用空格键实施点击) ![1.png](https://minesweeper-assist.googlecode.com/svn/wiki/1.png)
4. 选择右下角格子的左上角坐标：先将右下角格子点开，再将鼠标*大致*(无需借助放大镜)移动至右下角格子的左上角，点击Catch right-bottom origin按钮。(由于此时鼠标不可能处在该按钮上，因此你需要提前选中该按钮，再用空格键实施点击) ![2](https://minesweeper-assist.googlecode.com/svn/wiki/2_new.png)
5. 切换至扫雷游戏，F2重置游戏，再点击Minesweeper Assist主界面上的Start按钮，即开始自动扫雷。

#### 扩展参数说明
 * Click type: 无需说明
 * Reaction Time: 每次鼠标点击后，过多长时间开始进行推理，该时间设置过短可能导致读屏幕时，格子尚未完全翻开，于是导致推理错误。
 * Move 1/10/20 grids time: 鼠标移动1/10/20格子的长度所需要的时间，移动其他长度所需要的时间根据这三个值二次差值得到。适当的设置可以使鼠标的移动更加接近人类。
 * Speed change ratio: 鼠标每次移动 终点速率/起点速率 的值，该移动过程鼠标匀变速运动。
