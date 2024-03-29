import * as React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import { CssBaseline } from "@mui/material";
import { makeStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import { useNavigate } from "react-router-dom";
import { isUserAuthenticated } from "../auth/auth.js";
import { Navigate } from "react-router-dom";

const useStyles = makeStyles(() => ({
  appBar: {
    position: "fixed",
    top: 0,
  },
  heroContent: {
    padding: "8rem 0 4rem",
  },
  title: { flexGrow: 1 },
  actions: {
    marginTop: "2rem",
  },
  main: { margin: "1rem 0 0" },
}));

export default function LandingPage() {
  const navigate = useNavigate();
  const classes = useStyles();
  if (isUserAuthenticated()) return <Navigate to='/app' replace />;

  return (
    <Box sx={{ flexGrow: 1, width: "90%", margin: "0 auto" }}>
      <CssBaseline />
      <AppBar position='fixed' className={classes.appBar}>
        <Toolbar>
          <Typography variant='h6' sx={{ flexGrow: 1 }}>
            Trading Simulator
          </Typography>
          <Button color='inherit' onClick={() => navigate("/login")}>
            Login
          </Button>
          <Button color='inherit' onClick={() => navigate("/register")}>
            Register
          </Button>
        </Toolbar>
      </AppBar>
      <div className={classes.heroContent}>
        <div className={classes.title}>
          <Typography variant='h3' color='textPrimary' gutterBottom>
            Master the art of trading
          </Typography>
          <Typography variant='h6' color='textSecondary' paragraph>
            Learn to trade without risking real money. Our trading simulator
            allows you to practice trading stocks in real market conditions with
            starting capital of <strong>$30,000</strong>.
          </Typography>
        </div>
        <div className={classes.actions}>
          <Button
            variant='contained'
            sx={{ marginRight: "0.5rem" }}
            onClick={() => navigate("/register")}
          >
            Register for free
          </Button>
          <Button
            variant='outlined'
            sx={{ marginLeft: "0.5rem" }}
            onClick={() => {
              window.open(
                "https://www.ig.com/en/learn-to-trade/ig-academy/courses"
              );
            }}
          >
            Check out trading courses
          </Button>
        </div>
      </div>
      <div className={classes.main}>
        <Typography variant='h4' sx={{ marginBottom: "1rem" }}>
          Why Trading Simulator?
        </Typography>
        <Grid container sx={{ display: "flex" }}>
          <Grid item xs={12} md={6} lg={6} sx={{ paddingBottom: "1rem" }}>
            <Typography variant='p'>
              Anyone can create a free account and get started with virtual
              trading on over <strong>7,000 stocks</strong>. Our app offers an
              exciting way for beginners to learn the ins and outs of the stock
              market, while also providing a safe space for experienced traders
              to test out new strategies. <br />
              With our simulator, you can{" "}
              <strong>simulate buying and selling stocks</strong>,{" "}
              <strong>set stop-loss</strong> and{" "}
              <strong>take-profit orders</strong>, and{" "}
              <b>monitor your portfolio's performance</b> over time. You'll have
              access to <strong>real-time market data</strong>,{" "}
              <strong>charts</strong>, and <strong>news</strong>, so you can
              stay up-to-date with the latest market trends. <br />
              Our platform is easy to use, and you can get started with just a
              few clicks. <b>No experience is necessary</b>, and our virtual
              trading platform is designed to be user-friendly and intuitive.
              Join the thousands of traders who have already signed up for our
              simulator and take the first step towards becoming a successful
              trader.
              <br />
              <br />
              <b>
                Sign up for a free account today and start trading with virtual
                funds!
              </b>
            </Typography>
          </Grid>
          <Grid
            item
            xs={false}
            sm={false}
            md={6}
            lg={6}
            sx={{
              backgroundImage:
                "url(https://images.unsplash.com/photo-1650959828226-f9d53a7c1f64?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format)",
              backgroundSize: "cover",
              backgroundRepeat: "no-repeat",
              backgroundPosition: "center center",
              marginTop: "-5rem",
              height: "500px",
            }}
          />
        </Grid>
      </div>
    </Box>
  );
}
