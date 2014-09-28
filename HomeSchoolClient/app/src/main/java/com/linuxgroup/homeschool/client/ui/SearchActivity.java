package com.linuxgroup.homeschool.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linuxgroup.homeschool.client.R;
import com.linuxgroup.homeschool.client.api.UserApi;
import com.linuxgroup.homeschool.client.model.Person;
import com.linuxgroup.homeschool.client.tasks.SimpleBackgroundTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends Activity {

    @InjectView(R.id.account)
    EditText et_account;

    @InjectView(R.id.search_friend)
    Button bt_search_friend;

    @InjectView(R.id.name)
    TextView tv_name;

    @InjectView(R.id.add_friend)
    Button bt_add_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.inject(this);

        setListener();
    }

    private void setListener() {
        bt_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = et_account.getText().toString().trim();

                new SimpleBackgroundTask<Person>(SearchActivity.this) {
                    @Override
                    protected Person onRun() {
                        Person person = UserApi.search(account);
                        return person;
                    }

                    @Override
                    protected void onSuccess(Person person) {
                        tv_name.setText(person.getName());
                        bt_add_friend.setVisibility(View.VISIBLE);
                    }
                }.execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}