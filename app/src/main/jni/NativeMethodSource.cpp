#include "com_example_pavle_vasiljevic_shoppinglist_JNIClass.h"



JNIEXPORT jint JNICALL Java_com_example_pavle_vasiljevic_shoppinglist_JNIClass_increment
  (JNIEnv * en, jobject obj, jint x){

  return ++x;

  }