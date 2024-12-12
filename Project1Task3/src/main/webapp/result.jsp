<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>State Information</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .container {
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            text-align: center;
        }
        h1 {
            font-size: 24px;
            color: #333;
        }
        p {
            font-size: 18px;
            color: #666;
        }
        img {
            max-width: 100%;
            height: auto;
            margin-top: 10px;
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>State Information</h1>
    <p><strong>State:</strong> <%= request.getAttribute("state")%></p>
    <p><strong>Population:</strong> <%= request.getAttribute("population")%></p>
    <p><strong>Capital:</strong> <%= request.getAttribute("capital")%></p>
    <p><strong>Song:</strong> <%= request.getAttribute("song")%></p>
    <h2>Bird</h2>
    <img src="<%= request.getAttribute("birdURL")%>" alt="State Bird">
    <p>Credit: <a href="https://statesymbolsusa.org/categories/bird">StateSymbolsUSA.org</a></p>
    <h2>Flag</h2>
    <img src="<%= request.getAttribute("flagURL")%>" alt="State Flag">
    <p>Credit: <a href="https://states101.com/flags">States101.com</a></p>
    <form action="getAnStateInformation" method="GET">
        <label for="letter">Select another state:</label>
        <select name="state">
            <option value="Alabama" selected>Alabama</option>
            <option value="Alaska">Alaska</option>
            <option value="Arizona" >Arizona</option>
            <option value="Arkansas">Arkansas</option>
            <option value="California">California</option>
            <option value="Colorado">Colorado</option>
            <option value="Connecticut">Connecticut</option>
            <option value="Delaware">Delaware</option>
            <option value="Florida">Florida</option>
            <option value="Georgia" >Georgia</option>
            <option value="Hawaii">Hawaii</option>
            <option value="Idaho">Idaho</option>
            <option value="Illinois">Illinois</option>
            <option value="Indiana">Indiana</option>
            <option value="Iowa">Iowa</option>
            <option value="Kansas">Kansas</option>
            <option value="Kentucky" >Kentucky</option>
            <option value="Louisiana">Louisiana</option>
            <option value="Maine">Maine</option>
            <option value="Maryland">Maryland</option>
            <option value="Massachusetts">Massachusetts</option>
            <option value="Michigan">Michigan</option>
            <option value="Minnesota">Minnesota</option>
            <option value="Mississippi" >Mississippi</option>
            <option value="Missouri">Missouri</option>
            <option value="Montana">Montana</option>
            <option value="Nebraska">Nebraska</option>
            <option value="Nevada">Nevada</option>
            <option value="New Hampshire">New Hampshire</option>
            <option value="New Jersey">New Jersey</option>
            <option value="New Mexico" >New Mexico</option>
            <option value="New York">New York</option>
            <option value="North Carolina">North Carolina</option>
            <option value="North Dakota">North Dakota</option>
            <option value="Ohio">Ohio</option>
            <option value="Oklahoma">Oklahoma</option>
            <option value="Oregon">Oregon</option>
            <option value="Pennsylvania" >Pennsylvania</option>
            <option value="Rhode Island">Rhode Island</option>
            <option value="South Carolina">South Carolina</option>
            <option value="South Dakota">South Dakota</option>
            <option value="Tennessee">Tennessee</option>
            <option value="Texas">Texas</option>
            <option value="Utah">Utah</option>
            <option value="Vermont" >Vermont</option>
            <option value="Virginia">Virginia</option>
            <option value="Washington">Washington</option>
            <option value="West Virginia">West Virginia</option>
            <option value="Wisconsin">Wisconsin</option>
            <option value="Wyoming">Wyoming</option>
        </select>
        <br><br>
        <input type="submit" value="Continue">
    </form>
    <hr>
    <p>Or enter an input:</p>
    <form id="customStateForm" action="getAnStateInformation" method="GET">
        <input type="text" name="userInput" id="customStateInput" placeholder="Enter an input:">
        <input type="submit" name="submitCustom" value="SubmitCustom">
    </form>
</div>
</body>
</html>
