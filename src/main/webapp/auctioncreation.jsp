<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Auction | BuyMe Vehicles</title>
    <link rel="stylesheet" href="auctioncreatestyle.css"> 
</head>
<body>
    <div class="form-container">
        <h2>Create Vehicle Auction</h2>
        <form action="CreateAuctionServlet" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="make">Make:</label>
                <input type="text" id="make" name="make" placeholder="e.g., Toyota" required>
            </div>
            <div class="form-group">
                <label for="model">Model:</label>
                <input type="text" id="model" name="model" placeholder="e.g., Corolla" required>
            </div>
            <div class="form-group">
                <label for="year">Year:</label>
                <input type="number" id="year" name="year" placeholder="e.g., 2020" required>
            </div>
            <div class="form-group">
                <label for="color">Color:</label>
                <input type="text" id="color" name="color" placeholder="e.g., Red" required>
            </div>
            <div class="form-group">
                <label for="size">Size:</label>
                <input type="text" id="size" name="size" placeholder="e.g., Large" required>
            </div>
            <div class="form-group">
                <label for="mileage">Mileage:</label>
                <input type="number" id="mileage" name="mileage" placeholder="e.g., 50000" required>
            </div>
            <div class="form-group">
			    <label for="category">Category:</label>
			    <select id="category" name="categoryID" required>
			        <option value="">Select a category</option>
			        
			        <option value="1">Sedan</option>
			        <option value="2">Coupe</option>
			        <option value="3">Sport</option>
			        <option value="4">Super</option>
			        <option value="5">Truck</option>
			        <option value="6">SUV</option>
			        <option value="7">Minivan</option>
			        <option value="8">Convertible</option>
			        <option value="9">Hatchback</option>
			        <option value="10">Motorcycle</option>
			    </select>
			</div>
            <div class="form-group">
                <label for="vin">VIN:</label>
                <input type="text" id="vin" name="vin" placeholder="e.g., 1HGBH41JXMN109186" required>
            </div>
            <div class="form-group">
                <label for="startingBid">Starting Bid:</label>
                <input type="number" id="startingBid" name="initialPrice" step="0.01" placeholder="e.g., 5000.00" required>
            </div>
            <div class="form-group">
                <label for="reservePrice">Secret Minimum Price:</label>
                <input type="number" id="reservePrice" name="secretMinimum" step="0.01" placeholder="e.g., 10000.00" required>
            </div>
            <div class="form-group">
                <label for="bidIncrement">Bid Increment:</label>
                <input type="number" id="bidIncrement" name="bidIncrement" step="0.01" placeholder="e.g., 100.00" required>
            </div>
            <div class="form-group">
                <label for="closingTime">Closing Time:</label>
                <input type="datetime-local" id="closingTime" name="closingTime" required>
            </div>
            <div class="form-group">
                <label for="vehicleImage">Vehicle Image:</label>
                <input type="file" id="vehicleImage" name="image" required>
            </div>
            <button type="submit">Create Auction</button>
        </form>
        <a href="dashboard.jsp" class="return-home-btn">Return Home</a>
    </div>
</body>
</html>
