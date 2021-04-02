package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class NoticeActivity extends AppCompatActivity {

    private final int REQUEST_WIDTH = 1024;
    private final int REQUEST_HEIGHT = 512;
    int lastClickedPosition = 0;

    private ArrayList<NoticeItem> mGroupList = null;
    private ArrayList<ArrayList<NoticeItem>> mChildList = null;
    private ExpandableListView mListView;

    Bitmap bitmap;
    Bitmap bitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        String drawablePath1 = getURLForResource(R.drawable.mokup_1);
        Uri uri1 = Uri.parse(drawablePath1);
        bitmap = resize(this,uri1,500);

        String drawablePath2 = getURLForResource(R.drawable.mokup_2);
        Uri uri2 = Uri.parse(drawablePath2);
        bitmap2 = resize(this,uri2,500);

        setLayout();
        setList();

    }

    private void setList(){
        mGroupList = new ArrayList<NoticeItem>();
        mChildList = new ArrayList<ArrayList<NoticeItem>>();

        mGroupList.add(new NoticeItem("세련 서비스 출시안내", "2021-01-01", "세련 서비스가 1월 2일부로 론칭 되었습니다.\n앞으로 함께 세련된 이웃생활을 시작해 보아요!",null));
        mGroupList.add(new NoticeItem("세련 운영정책 변경 안내", "2021-01-02", "세련 서비스의 회원가입 절차 및 서비스 이용약관이 1월1일부로 변경되었습니다.\n세련 이웃님들께서는 해당 내용의 확인을 부탁드립니다.",bitmap));
        mGroupList.add(new NoticeItem("세련앱 잠금 서비스 업데이트", "2021-01-03", "세련앱 내의 앱 잠금 기능이 추가 되었어요.\n간편하게 지문인식을 통해 사용해보세요!",bitmap2));

        mChildList.add(mGroupList);
        mChildList.add(mGroupList);
        mChildList.add(mGroupList);

        Collections.reverse(mGroupList);
        Collections.reverse(mChildList);

        mListView.setAdapter(new NoticeExpendableAdapter(this, mGroupList, mChildList));

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // 선택 한 groupPosition의 펼침/닫힘 상태 체크
                Boolean isExpand = (!mListView.isGroupExpanded(groupPosition));

                // 이 전에 열려있던 group 닫기
                mListView.collapseGroup(lastClickedPosition);

                if(isExpand){
                    mListView.expandGroup(groupPosition);
                }
                lastClickedPosition = groupPosition;
                return true;
            }
        });


        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                return false;
            }
        });

        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
    }

    private String getURLForResource(int resId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resId).toString();
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    private void setLayout(){
        mListView = (ExpandableListView)findViewById(R.id.elv_list);
    }

    public void onClick_back(View v){
        finish();
    }

    public void onBackPressed() {
        finish();
    }
}
