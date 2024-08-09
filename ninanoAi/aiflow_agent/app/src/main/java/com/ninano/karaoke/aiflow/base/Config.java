/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninano.karaoke.aiflow.base;

public abstract class Config {
    // copy this keys from your developer dashboard
    public static final String ACCESS_TOKEN = "bab8e71ca81a44bb914e1dd85db51be2";

    public static final LanguageConfig[] languages = new LanguageConfig[]{
            new LanguageConfig("ko", ACCESS_TOKEN),
            new LanguageConfig("ja", ACCESS_TOKEN),
            new LanguageConfig("zh-CN", ACCESS_TOKEN)
    };

//    public static final LanguageConfig[] languages = new LanguageConfig[]{
//            new LanguageConfig("en", ACCESS_TOKEN),
//            new LanguageConfig("ru", ACCESS_TOKEN),
//            new LanguageConfig("de", ACCESS_TOKEN),
//            new LanguageConfig("pt", ACCESS_TOKEN),
//            new LanguageConfig("pt-BR", ACCESS_TOKEN),
//            new LanguageConfig("es", ACCESS_TOKEN),
//            new LanguageConfig("fr", ACCESS_TOKEN),
//            new LanguageConfig("it", ACCESS_TOKEN),
//            new LanguageConfig("ja", ACCESS_TOKEN),
//            new LanguageConfig("ko", ACCESS_TOKEN),
//            new LanguageConfig("zh-CN", ACCESS_TOKEN),
//            new LanguageConfig("zh-HK", ACCESS_TOKEN),
//            new LanguageConfig("zh-TW", ACCESS_TOKEN),
//    };

    public static final String[] events = new String[]{
            "hello_event",
            "goodbye_event",
            "how_are_you_event"
    };
}
