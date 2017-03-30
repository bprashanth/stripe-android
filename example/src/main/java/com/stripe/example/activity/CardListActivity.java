package com.stripe.example.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jakewharton.rxbinding.view.RxView;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.example.R;
import com.stripe.example.adapter.CardListAdapter;
import com.stripe.example.adapter.PollingAdapter;
import com.stripe.example.controller.ErrorDialogHandler;
import com.stripe.example.controller.ProgressDialogController;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CardListActivity extends AppCompatActivity {

    private static final String FUNCTIONAL_SOURCE_PUBLISHABLE_KEY =
            "pk_test_vOo1umqsYxSrP5UXfOeL3ecm";
    private CardInputWidget mCardInputWidget;
    private CardListAdapter mCardListAdapter;
    private ErrorDialogHandler mErrorDialogHandler;
    private ProgressDialogController mProgressDialogController;
    private Stripe mStripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        RecyclerView cardRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);

        mErrorDialogHandler = new ErrorDialogHandler(this.getSupportFragmentManager());
        mProgressDialogController = new ProgressDialogController(this.getSupportFragmentManager());

        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_widget_just_cards);
        mCardListAdapter = new CardListAdapter();
        mStripe = new Stripe(this);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cardRecyclerView.setHasFixedSize(true);
        cardRecyclerView.setLayoutManager(linearLayoutManager);
        cardRecyclerView.setAdapter(mCardListAdapter);

        RxView.clicks(findViewById(R.id.btn_make_card_source))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        makeCardSource(mCardInputWidget.getCard());
                    }
                });
    }

    private void makeCardSource(@Nullable Card card) {
        if (card == null) {
            return;
        }

        final SourceParams cardParams = SourceParams.createCardParams(card);
        Observable<Source> cardSourceObservable = Observable.fromCallable(
                new Callable<Source>() {
                    @Override
                    public Source call() throws Exception {
                        return mStripe.createSourceSynchronous(cardParams,
                                FUNCTIONAL_SOURCE_PUBLISHABLE_KEY);
                    }
                });

        cardSourceObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialogController.startProgress();
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialogController.finishProgress();
                    }
                })
                .subscribe(
                    new Action1<Source>() {
                        @Override
                        public void call(Source source) {
                            mCardListAdapter.addItem(source);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mErrorDialogHandler.showError(throwable.getLocalizedMessage());
                        }
                    });
    }
}
