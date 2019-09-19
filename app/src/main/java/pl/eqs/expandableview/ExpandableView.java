package pl.eqs.expandableview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableView extends LinearLayout {

    private TextView labelView;
    private LinearLayout contentView;

    private boolean isExpanded = false;
    private int contentHeight = 0;
    private final Handler h;

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.expandable_view, this);

        this.labelView = (TextView) findViewById(R.id.label);
        this.contentView = (LinearLayout) findViewById(R.id.content);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ExpandableView, 0, 0);

        try {
            labelView.setText(a.getString(R.styleable.ExpandableView_label));

            contentView.addView((LinearLayout) LayoutInflater.from(getContext()).inflate(a
                    .getResourceId(R.styleable.ExpandableView_content, 0), null));
        } finally {
            a.recycle();
        }

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        contentHeight = contentView.getHeight();  // Ahaha!  Gotcha
                        contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                        lp.height = 0;
                        contentView.setLayoutParams(lp);
                        contentView.setVisibility(View.GONE);
                    }

                });

        h = new Handler();
        labelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                if(isExpanded) {
                    more();
                } else {
                    less();
                }
            }
        });
    }

    public void more() {
        contentView.setVisibility(View.VISIBLE);
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                lp.height = lp.height + 50;
                contentView.setLayoutParams(lp);

                h.postDelayed(this, 20);

                if(lp.height >= contentHeight) {
                    h.removeCallbacksAndMessages(null);
                }
            }
        }, 20);
    }

    public void less() {
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                lp.height = lp.height - 50;
                contentView.setLayoutParams(lp);

                h.postDelayed(this, 20);
                if(lp.height <= 0) {
                    h.removeCallbacksAndMessages(null);
                    contentView.setVisibility(View.GONE);
                }
            }
        }, 20);
    }

}
