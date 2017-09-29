/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljpww72729.atblink;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class TTS {

    private static TextToSpeech textToSpeech;

    public static void init(final Context context) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
                    if (status == TextToSpeech.SUCCESS) {
                        // Set preferred language to US english.
                        // Note that a language may not be available, and the result will indicate this.
                        int result = textToSpeech.setLanguage(Locale.ENGLISH);
                        // Try this someday for some interesting results.
                        // int result mTts.setLanguage(Locale.FRANCE);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            // Lanuage data is missing or the language is not supported.
                            Log.e(TAG, "Language is not available.");
                        } else {
                            // Check the documentation for other possible result codes.
                            // For example, the language may be available for the locale,
                            // but not for the specified country and variant.

                            // The TTS engine has been successfully initialized.
                            // Allow the user to press the button for the app to speak again.
                        }
                    } else {
                        // Initialization failed.
                        Log.e(TAG, "Could not initialize TextToSpeech.");
                    }
                }
            });
        }
    }

    public static void speak(final String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
    }
}
