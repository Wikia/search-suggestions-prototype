define("log", [], function() {
    function log( x ) {
        if( console && console.log ) {
            console.log( x );
        }
    }
    return log;
});