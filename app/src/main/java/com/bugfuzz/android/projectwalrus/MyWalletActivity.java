package com.bugfuzz.android.projectwalrus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyWalletActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mywallet_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_terminalActivity:
                Intent intent = new Intent(this, TerminalActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

        private final LayoutInflater inflater;
        String[] cardtitle = {

                "Office door",
                "Office elevator",
                "Server room",
                "Garage parking",
                "Secret room",
                "Red team1",
                "Red team2",
                "Red team3",
                "Red team4",
                "Random card"

        };

        final String[] cardUID = {

                "252500000251566",
                "252500000251567",
                "252500000251568",
                "252500000251569",
                "252500000251516",
                "252500000268987",
                "252500151626516",
                "252500012566512",
                "252500011259789",
                "252500000287008"

        };

        int[] cardimage = {
                R.drawable.hid,
                R.drawable.hid,
                R.drawable.mifare,
                R.drawable.hid,
                R.drawable.mifare,
                R.drawable.mifare,
                R.drawable.hid,
                R.drawable.mifare,
                R.drawable.hid,
                R.drawable.hid

        };

        public CardAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }
        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.activity_mywallet_card_row,parent,false);
            final CardViewHolder holder = new CardViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailedCardViewActivity.sendCardDetails(MyWalletActivity.this,
                            holder._cardTitle.getText().toString(),
                            holder._cardUID.getText().toString());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {

            holder._cardTitle.setText(cardtitle[position]);
            holder._cardUID.setText(cardUID[position]);
            holder._imgCard.setImageResource(cardimage[position]);
        }

        @Override
        public int getItemCount() {
            return cardtitle.length;
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {

            ImageView _imgCard;
            TextView _cardTitle;
            TextView _cardUID;

            public CardViewHolder(View itemView) {

                super(itemView);
                _imgCard = (ImageView) itemView.findViewById(R.id.imgCard);
                _cardTitle = (TextView) itemView.findViewById(R.id.txtCardTitle);
                _cardUID = (TextView) itemView.findViewById(R.id.txtCardUID);
            }
        }
    }

    private RecyclerView rview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);

        rview = (RecyclerView) findViewById(R.id.my_recycler_view);
        rview.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        rview.setItemAnimator(new DefaultItemAnimator());
        rview.setAdapter(new CardAdapter(this));
        rview.setHasFixedSize(true);
        rview.setLayoutManager(new LinearLayoutManager(MyWalletActivity.this));

    }
}
