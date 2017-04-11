
# 高可配置SlidingMenu
ViewGroup + ViewDragHelper 实现的SlidingMenu ，简单配置可实现任意效果
### QQ效果

![QQ](https://github.com/MrJiao/SlidingMenu/blob/master/github_res/QQ.gif)
[查看全部代码](https://github.com/MrJiao/SlidingMenu/blob/master/app/src/main/java/jackson/com/slidingmenu/QQActivity.java)

设置代码如下


```
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(),new MenuFragment(),getFragmentManager(), 870)
        .setMenuStartLeft(-500)
                .build();
```

### 酷狗效果
![kugou](https://github.com/MrJiao/SlidingMenu/blob/master/github_res/kugou.gif)
[查看全部代码](https://github.com/MrJiao/SlidingMenu/blob/master/app/src/main/java/jackson/com/slidingmenu/KugouActivity.java)

设置代码如下

```
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(), new MenuFragment(), getFragmentManager(), 850)
        .setMenuStartLeft(-350)
        .setOnViewChangedListener(new ScaleChange())
        .build();
```


### 平移效果

![other](https://github.com/MrJiao/SlidingMenu/blob/master/github_res/other.gif)

[查看全部代码](https://github.com/MrJiao/SlidingMenu/blob/master/app/src/main/java/jackson/com/slidingmenu/Other1Activity.java)

设置代码如下

```
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(),new MenuFragment(),getFragmentManager(), 870)
                .setContentEndLeft(0)
                .setCover(true)
                .build();
```


### 旋转效果
![roating](https://github.com/MrJiao/SlidingMenu/blob/master/github_res/roating.gif)

[查看全部代码](https://github.com/MrJiao/SlidingMenu/blob/master/app/src/main/java/jackson/com/slidingmenu/XuanZhuanActivity.java)

设置代码如下

```
        SlidingMenu sm = (SlidingMenu) findViewById(R.id.sm);
        sm.getBuilder(new ContentFragment(), new MenuFragment(), getFragmentManager(), 850)
        .setMenuStartLeft(0)
        .setOnViewChangedListener(new ScaleChange())
        .build();
```


# API介绍

### 类 SlidingMenu.Builder
介绍：这个类提供对SlidingMenu的所有设置

默认menu结束位置为0，content开始位置为0，所以只支持左向右滑动

![image](https://github.com/MrJiao/SlidingMenu/blob/master/github_res/api.bmp)
  |  
|api         | 介绍   |
| -----------|:-----:|
Builder(View content, View menu, int menuWidth) | 构造方法
Builder(Fragment content, Fragment menu, FragmentManager fragmentManager, int menuWidth) | 构造方法
setMenuStartLeft(int menuStartLeft) | 设置menu的开始位置，通常为负数 单位px
setContentEndLeft(int contentEndLeft) | 设置content的结束位置，通常为正数 单位px
setCover(boolean isCover) | 如果为true 则menu在content前面 默认false
setOnViewChangedListener(OnViewChangedListener onViewChangedListener) | view状态变化监听器，通常在里面对view进行透明渐变、旋转、缩放等动画
setOnStateChangedListener(OnStateChangedListener stateChangedListener) | 状态监听


### 注意：
setMenuStartLeft(int menuStartLeft) 和 setContentEndLeft(int contentEndLeft)
不能同时设置 Menu起始位置和Content结束位置为0 

