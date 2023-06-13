# Trading Simulator

Trading Simulator is a web application developed using **Java Spring Boot**, **React.js** (along with **Material UI**), and **MySQL** database. It provides users with a simulated trading experience, allowing them to perform various trading activities.

## Features

1. **User Authentication**: Users can register and log in to the application. Upon registration, users receive a confirmation email to verify their account.

2. **Dashboard**: Provides a quick overview of the user's trading activities and recommendations. It displays basic information and summary statistics to give users a glance at their portfolio performance.

3. **Portfolio**: Displays the portfolio performance chart, list of stocks held by the user, cash balance, portfolio value, today's change, annual return, and buy power. Users can track their investments and assess the performance of their holdings.

4. **Trade**: Allows users to explore and trade stocks. Users can search for specific stocks by symbol or name. Upon selecting a stock, detailed information is provided such as price, bid/ask price, day's high/low, volume, a price chart etc. Users can choose the desired time range for the chart (**1D**, **1M**, **6M**, **1Y**, **5Y**, or **MAX**). A trade form enables users to initiate trades by selecting the quantity, order type, and order duration.

5. **News**: Provides financial news related to stocks. Users can stay updated with the latest news and developments in the financial markets.

6. **Transactions**: Displays a history of all the transactions made by the user, including all trades. Users can review their transaction history to track their trading activities.

7. **Settings**: Allows users to modify their basic account settings, such as changing their name or password.

## Screenshots

*Will be added upon project completion.*

## Prerequisites

Before running the application, ensure that the following software is installed on your system:

- Java Development Kit (JDK)
- Node.js
- MySQL

## Installation

1. Clone the repository.
2. Navigate to the project's `backend` directory.
3. Configure the application by creating the `application.properties` file. Add the database connection details and other relevant configurations such as API keys.
4. Install the backend dependencies and build the Spring Boot application.
5. Navigate to the `frontend` directory and install the frontend dependencies: `npm install`
6. Run the React.js frontend: `npm start`
7. Access the application at `http://localhost:3000` in your web browser.

## Contributors

- [Nikola Grujic](https://github.com/nikolatechie)
