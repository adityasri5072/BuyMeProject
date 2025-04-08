<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Place Your Bid</title>
</head>
<body>
    <h1>Place Your Bid</h1>
    <script>
        function validateForm() {
            var auctionId = document.getElementById('auctionId').value;
            var userId = document.getElementById('userId').value;
            var bidAmount = document.getElementById('bidAmount').value;
            var maxAutoBidAmount = document.getElementById('maxAutoBidAmount').value;
            var bidIncrement = document.getElementById('bidIncrement').value;

            if (!auctionId || !userId || !bidAmount) {
                alert("All fields must be filled out correctly.");
                return false;
            }

            if (maxAutoBidAmount && bidIncrement && parseFloat(bidIncrement) <= 0) {
                alert("Bid increment must be a positive number if specified.");
                return false;
            }
            
            return true;
        }
    </script>

    <form action="ProcessBidServlet" method="POST" onsubmit="return validateForm();">
        <input type="hidden" name="auctionId" value="<%= request.getParameter("auctionId") %>">
        <input type="hidden" name="userId" value="<%= session.getAttribute("userID") %>">
        <label>Your Bid: <input type="number" name="bidAmount" required></label>
        <label>Upper Limit Auto-Bid(Optional): <input type="number" name="maxAutoBidAmount"></label>
        <label>Bid Increment(Optional): <input type="number" id="bidIncrement" name="bidIncrement"></label>
        <button type="submit">Submit Bid</button>
    </form>

    <a href="dashboard.jsp">Back to Dashboard</a>
</body>
</html>
