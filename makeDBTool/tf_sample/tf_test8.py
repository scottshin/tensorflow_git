from __future__ import absolute_import, division, print_function, unicode_literals

# tensorflow and tf.keras import
import tensorflow as tf
from tensorflow import keras

# import helper lib
import numpy as np
import matplotlib.pyplot as plt

print( tf.__version__)


fashion_mnist = keras.datasets.fashion_mnist
(train_images, train_labels), (test_images, test_labels) = fashion_mnist.load_data()



class_names = [ 'T-shirt/top', 'Trouser', 'Pullover', 'Dress', 'Coat', 'Sandal', 'Shirt', 'Sneaker', 'Bag', 'Ankle boot' ]

train_images.shape

len(train_labels)

test_images.shape

len(test_images)


# prepare data

#plt.figure()
#plt.imshow(train_images[0])
#plt.colorbar()
#plt.grid(False)
#plt.show()


#
train_images = train_images / 255.0
test_images = test_images / 255.0 
#
#plt.figure(figsize=(10,10))
#for i in range(25):
#    plt.subplot(5,5,i+1)
#    plt.xticks([])
#    plt.yticks([])
#    plt.grid(False)
#    plt.imshow(train_images[i], cmap=plt.cm.binary)
#    plt.xlabel(class_names[train_labels[i]])
#plt.show()

# 2array to seq array
model = keras.Sequential([
    keras.layers.Flatten(input_shape=(28, 28)), 
    keras.layers.Dense(128, activation=tf.nn.relu),
    keras.layers.Dense(10, activation=tf.nn.softmax)
])

model.compile(optimizer='adam',
            loss='sparse_categorical_crossentropy',
            metrics=['accuracy'])

#
model.fit(train_images, train_labels, epochs=5)


#
test_loss, test_acc = model.evaluate(test_images, test_labels)
print('테스트 정확도:', test_acc )


# 훈련된 모델을 사용하여 이미지에 대한 예측을 만들 수 있다. 
predictions = model.predict(test_images)

#
print( "Predictions : ", predictions[0] )


print ( "arg max:", np.argmax(predictions[0]) )

print ( "best val ", test_labels[0])


