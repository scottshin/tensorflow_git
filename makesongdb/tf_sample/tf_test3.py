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

print (' train images : ', train_images.shape )
print (' train lables count : ', len(train_labels) )

print (' test images : ' , test_images.shape)
print (' test iamges count : ', len(test_images) )


# prepare data

plt.figure()
plt.imshow(train_images[0])
plt.colorbar()
plt.grid(False)
plt.show()
