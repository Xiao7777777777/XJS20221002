package com.example.xjs20221002gesturepassworddemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawCircle extends View {

    //定义默认常量
    private static final int DEFAULT_CELL_WIDTH = 200;
    private static final int DEFAULT_CELL_STROKE_WIDTH = 10;
    private static final int DEFAULT_SPACE = 100;

    //九宫格数组
    private final Cell[] mCells = new Cell[9];
    private final StringBuffer mSbSelected = new StringBuffer(20);
    //直径
    private int mCellWidth;
    //半径
    private int mCellRadius;
    //边框宽度
    private int mCellStrokeWidth;
    //空白部分
    private int mSpace;
    //定义画笔
    private Paint mPaintNormal;
    private Paint mPaintSelected;
    private float mCurrentX;
    private float mCurrentY;
    //判断是否结束的标识
    private boolean mFinish = false;

    public DrawCircle(Context context) {
        super(context);
        init();
    }

    public DrawCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //初始化画笔
        mCellWidth = DEFAULT_CELL_WIDTH;
        mCellRadius = DEFAULT_CELL_WIDTH >> 1;
        mCellStrokeWidth = DEFAULT_CELL_STROKE_WIDTH;
        mSpace = DEFAULT_SPACE;

        mPaintNormal = new Paint();
        mPaintNormal.setColor(Color.WHITE);
        mPaintNormal.setStrokeWidth(mCellStrokeWidth);
        mPaintNormal.setStyle(Paint.Style.STROKE);
        mPaintNormal.setAntiAlias(true);

        mPaintSelected = new Paint();
        mPaintSelected.setColor(Color.CYAN);
        mPaintSelected.setStrokeWidth(mCellStrokeWidth);
        mPaintSelected.setStyle(Paint.Style.STROKE);
        mPaintSelected.setAntiAlias(true);

        Cell cell;
        float x;
        float y;
        //计算每个格子的坐标
        for (int i = 0; i < 9; i++) {
            x = mSpace * (i % 3 + 1) + mCellRadius + mCellWidth * (i % 3);
            y = mSpace * (i / 3 + 1) + mCellRadius + mCellWidth * (i / 3);

            cell = new Cell(x, y);
            mCells[i] = cell;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCell(canvas);
        drawLine(canvas);
    }

    //绘制连接线
    private void drawLine(Canvas canvas) {
        if ("".equals(mSbSelected.toString())) {
            return;
        }
        String[] selectedIndexes = mSbSelected.toString().split(",");
        Cell cell = mCells[Integer.valueOf(selectedIndexes[0])];
        Cell nextCell;

        //绘制每两个格子中心点之间的连接线
        if (selectedIndexes.length > 1) {
            for (int i = 1; i < selectedIndexes.length; i++) {
                nextCell = mCells[Integer.valueOf(selectedIndexes[i])];
                canvas.drawLine(cell.getCenterX(), cell.getCenterY(), nextCell.getCenterX(), nextCell.getCenterY(), mPaintSelected);

                cell = nextCell;
            }
        }
        //绘制格子到其他空白位置的连接线
        if (!mFinish) {
            canvas.drawLine(cell.getCenterX(), cell.getCenterY(), mCurrentX, mCurrentY, mPaintSelected);
        }

    }

    private void drawCell(Canvas canvas) {
        for (int i = 0; i < 9; i++) {
            canvas.drawCircle(mCells[i].getCenterX(), mCells[i].getCenterY(), mCellRadius,
                    mCells[i].isSelected() ? mPaintSelected : mPaintNormal);
        }

    }

    //处理点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果手指已经松开，则所有格子变为初始状态

                handleDownEvent(event);
                break;
            //松开则结束
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < 9; i++) {
                    mCells[i].setSelected(false);
                }
                mFinish = false;
                mSbSelected.delete(0, mSbSelected.length());
                invalidate();
                return false;
            case MotionEvent.ACTION_MOVE:
                handleMoveEvent(event);
                break;
        }

        //表示已处理，不向上传递
        return true;
    }

    //处理手指移动的事件
    private void handleMoveEvent(MotionEvent event) {
        drawLine(event.getX(),event.getY());
    }

    //处理手指按下的事件
    private void handleDownEvent(MotionEvent event) {
        drawLine(event.getX(),event.getY());
    }
    public void drawLine(float x,float y){
        int index = findCellIndex(x, y);
        if (index != -1) {
            mCells[index].setSelected(true);
            mSbSelected.append(index).append(",");
        }
        invalidate();

        mCurrentX =x;
        mCurrentY =y;
    }

    //根据坐标判断点击的哪个格子
    private int findCellIndex(float x, float y) {
        float cellX;
        float cellY;
        int result = -1;

        for (int i = 0; i < 9; i++) {
            if (mCells[i].isSelected()) {
                continue;
            }

            //获取每个格子的坐标
            cellX = mCells[i].getCenterX();
            cellY = mCells[i].getCenterY();

            //计算按下的点到每个格子的距离
            float tempX = cellX - x;
            float tempY = cellY - y;
            float distance = (float) Math.sqrt(tempX * tempX + tempY * tempY);

            //如果点击的位置在某个格子的圆内
            if (distance < mCellRadius) {
                result = i;
                break;
            }
        }
        //返回该格子的位置
        return result;
    }

    public class Cell {
        float centerX;
        float centerY;
        boolean selected;

        public Cell(float centerX, float centerY) {
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
