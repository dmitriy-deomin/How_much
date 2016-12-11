package dmitriy.deomin.how_much;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iangclifton.android.floatlabel.FloatLabel;

public class User_kabinet extends Activity {
    String id_strung_edit_text;
    String pasvord_user;
    FloatLabel editText_psevdo_user;
    FloatLabel editText_eser_id;
    FloatLabel editText_pasvord;

    String old_id;
    String old_pasvord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_kabinet);

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        editText_psevdo_user = (FloatLabel)findViewById(R.id.editText_psevdo_user);
        editText_psevdo_user.getEditText().setTypeface(Main.face);
        editText_psevdo_user.getEditText().setText(Main.save_read("user_psevdo"));


        editText_eser_id = (FloatLabel)findViewById(R.id.editText_eser_id);
        editText_eser_id.getEditText().setTypeface(Main.face);



        editText_pasvord = (FloatLabel)findViewById(R.id.editText_pasvord);
        editText_pasvord.getEditText().setTypeface(Main.face);



        ((Button)findViewById(R.id.button_save_user_kabinet)).setTypeface(Main.face);


        ((RelativeLayout)findViewById(R.id.fon_user_kabinet)).setBackgroundColor(Main.FON);



        if(Main.ID_DEVISE.contains("&pazduplitel&")){
            String[] mas = Main.ID_DEVISE.split("&pazduplitel&");
            id_strung_edit_text = mas[0].toString();
            //если там ид устройства установим его неизменяемым
            if(id_strung_edit_text.equals(Main.ID_TELEFONA)){
                editText_eser_id.getEditText().setText(id_strung_edit_text);
            }else{
                //иначе поставим тектс
                editText_eser_id.getEditText().setText(id_strung_edit_text);
            }
            editText_pasvord.getEditText().setText(mas[1].toString());
        }else{
            //установим по умолчанию
            editText_eser_id.setLabel(Main.ID_TELEFONA);
            editText_pasvord.getEditText().setText(Main.PASVORD_TELEFONA);
        }

        old_id = editText_eser_id.getEditText().getText().toString();
        old_pasvord = editText_pasvord.getEditText().getText().toString();



        editText_eser_id.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>1){
                    id_strung_edit_text =  charSequence.toString();
                }else {
                    id_strung_edit_text = Main.ID_TELEFONA;
                    editText_eser_id.setLabel("Id (По умолчанию Ваш:"+id_strung_edit_text+")");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText_pasvord.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()<1){
                    editText_pasvord.setLabel("Пароль (По умолчанию Ваш:"+Main.PASVORD_TELEFONA+")");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void Save_data_user(View view) {

        boolean show_tost = true;

        //если юэер удалил все в ид, поставим по умолчанию
        if(editText_eser_id.getEditText().getText().toString().length()==0){
             editText_eser_id.getEditText().setText(id_strung_edit_text);
        }



            //если есть изенения в логинах будем дальше мутить
            if(!editText_eser_id.getEditText().getText().toString().equals(old_id)){
                show_tost = false;
                //если пароль не ввели постави по умолчанию
                if(editText_pasvord.getEditText().getText().toString().length()<1){
                  editText_pasvord.getEditText().setText(Main.PASVORD_TELEFONA);
                }
                    if(Main.RENAMES<Main.SIZE_RENAMES){
                        pasvord_user = editText_pasvord.getEditText().getText().toString();
                        Main.ID_DEVISE = id_strung_edit_text + "&pazduplitel&" + pasvord_user;
                        Main.save_value("ID_DEVISE", Main.ID_DEVISE);

                        Main.RENAMES++;
                        Main.save_value_int("RENAMES",Main.RENAMES);

                        Main.Toast("Сохраннено, осталось: "+String.valueOf(Main.SIZE_RENAMES-Main.RENAMES)+" измнений");
                    }else {
                        Main.Toast_error("Количество попыток изменений закончилось");
                    }

                this.finish();
            }

            //если есть изенения в пароле будем дальше мутить
            if(!editText_pasvord.getEditText().getText().toString().equals(old_pasvord)){
                show_tost = false;
                //если пароль не ввели поставим по умолчанию
                if(editText_pasvord.getEditText().getText().toString().length()<1){
                    editText_pasvord.getEditText().setText(Main.PASVORD_TELEFONA);
                }
                    if(Main.RENAMES<Main.SIZE_RENAMES){
                        pasvord_user = editText_pasvord.getEditText().getText().toString();
                        Main.ID_DEVISE = id_strung_edit_text + "&pazduplitel&" + pasvord_user;
                        Main.save_value("ID_DEVISE", Main.ID_DEVISE);

                        Main.RENAMES++;
                        Main.save_value_int("RENAMES",Main.RENAMES);

                        Main.Toast("Сохраннено, осталось: "+String.valueOf(Main.SIZE_RENAMES-Main.RENAMES)+" измнений");
                    }else {
                        Main.Toast_error("Количество попыток изменений закончилось");
                    }

                this.finish();
            }


        //псевдоним сохраним в любом случае
        Main.save_value("user_psevdo",((FloatLabel)findViewById(R.id.editText_psevdo_user)).getEditText().getText().toString());

        if(show_tost){
             Main.Toast("Псевдоним сохранен");
        }


        this.finish();
    }

}
