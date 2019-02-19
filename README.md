# Wegman-s-Scanner
<h2>The project uses Python to build a Barcode Detector along with a Barcode reader. The Barcode value is then stored in a CSV file. Moreover, this project uses the Wegman's API in order to collect data and access it to perform certain tasks.</h2>

<h3>Motive:</h3>
<p>Few days after grocery shopping, a customer doesn't remember hw many things that he had bought are left with him. Whether he finished using them or are they still there in his kitchen. Due to this problem, most of the time the customer cannot even decide what to eat or cook for that day. 
<p>This project focuses on helping the customer with that problem and help them with keeping a track of their groceries.
As soon as the customer goes back home, he/she can scan the barcode that is there on the bought item. These barcodes are stored in a separate database and are then used later in order to check whether the item is available for cooking the required dish.

<h3>How it Works: </h3>
<p>This project uses Wegman's API to obtain data that is being provided by Wegman's on their products, recipes, etc.
<p>The barcode detector uses image processing (gray scaling) in order to detect the barcode on the product. The image of the barcode that was being detected can be taken by pressing the space bar. This image is stored in a different file which accessed by the barcode reader in order to get the data from the barcode.
<p>The barcode Reader uses inbuilt python library called pyzbar in order to read the barcode and provide the final data. 
<p>The obtained data is stored in a CSV file.
[The barcode scanner and the barcode reader were both programmed in Python.]
<p>This CSV file is accessed by the Java Code that is using Wegman's API in order to access the data that is being provided by Wegman's. 
<p>The user/customer is asked for the dish he/she wants to cook.
<p>The barcode data provided by the user is used to check whether all the the ingridients for the dish are available with the user.
