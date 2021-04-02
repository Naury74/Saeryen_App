package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class OSS_License_Activity extends AppCompatActivity {

    ArrayList<OSS_Data> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oss_license);

        this.InitializeMovieData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final OSS_Adapter oss_adapter = new OSS_Adapter(this,list);

        listView.setAdapter(oss_adapter);

    }

    public void onClick_back(View v){
        finish();
    }

    public void InitializeMovieData()
    {
        list = new ArrayList<OSS_Data>();

        list.add(new OSS_Data("Copyright 2013 Square, Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.", "-  Picasso","https://github.com/square/picasso"));

        list.add(new OSS_Data("Copyright 2018 Chris Banes\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.", "-  PhotoView","https://github.com/Baseflow/PhotoView"));

        list.add(new OSS_Data("Copyright 2017 Ted Park\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.", "-  TedPermission","https://github.com/ParkSangGwon/TedPermission"));

        list.add(new OSS_Data("Copyright 2014 - 2020 Henning Dodenhof\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.", "-  CircleImageView","https://github.com/hdodenhof/CircleImageView"));

        list.add(new OSS_Data("Originally forked from edmodo/cropper.\n" +
                "\n" +
                "Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.\n" +
                "\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:\n" +
                "\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.", "-  Android Image Cropper","https://github.com/ArthurHub/Android-Image-Cropper"));
    }
}
