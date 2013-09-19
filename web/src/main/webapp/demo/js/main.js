$(function() {
    function log( x ) {
        if( console && console.log ) {
            console.log( x );
        }
    }
    var Client = function() {};
    Client.prototype ={
        getSuggestions: function( wiki, query, cb ) {
            $.getJSON( "/api/wiki/" + wiki + "/suggest/" + query, function( response ) {
                cb(response);
            } )
        }
    };
    var client = new Client();

    var SuggestionsViewModel = function() {};
    SuggestionsViewModel.prototype = {
        setQuery: function( query ) {
            if ( this.query === query ) return;
            this.query = query;
            this.sendQuery();
            this.trigger( "query changed", query );
        },

        setWiki: function( wiki ) {
            this.wiki = wiki;
            this.sendQuery();
            this.trigger( "wiki changed", wiki );
        },

        sendQuery: function() {
            var self = this;
            client.getSuggestions( this.wiki, this.query, function(res) {
                self.setResults(res);
            } );
        },

        setResults: function( results ) {
            this.results = results;
            this.setDisplayResults( results );
            this.trigger( "results changed", results );
        },


        setDisplayResults: function( results ) {
            this.displayResults = results;
            this.trigger( "displayResults changed", results );
        },
        getDisplayResults: function( ) {
            return this.displayResults;
        },

        // event emitter pattern
        on: function( event, cb ) {
            this._ev = this._ev || {};
            this._ev[event] = this._ev[event] || [];
            this._ev[event].push( cb );
        },
        trigger: function( ev, value ) {
            var handlers = this._ev[ev] || {};
            for( var i in handlers ) {
                try {
                    handlers[i](value);
                } catch(ex) {
                    log(ex);
                }
            }
        }
    };

    var SuggestionsView = function( viewModel, searchInput, dropdown, selectWiki ) {
        viewModel.on( "displayResults changed", function() {
            var results = viewModel.getDisplayResults();
            dropdown.empty();
            for( var i in results ) {
                var res = results[i];
                var html = '<li>\
                    <a href="' + results[i].url + '">\
                        <img class="search-suggest-image" src="' + res.thumbnail + '" />\
                        <span class="title">' + res.title + '</span>\
                        <span class="abstract"></span>\
                    </a>\
                </li>';
                $(html).appendTo(dropdown);
            }
        });
        searchInput.on( "change", function () {
            var value = searchInput.val();
            viewModel.setQuery( value );
        });
        searchInput.on( "keypress", function () {
            window.setTimeout(function() {
                var value = searchInput.val();
                viewModel.setQuery( value );
            }, 0);
        });
        searchInput.on( "keyup", function () {
            var value = searchInput.val();
            viewModel.setQuery( value );
        });
        function updateWiki() {
            viewModel.setWiki( parseInt( selectWiki.val() ) );
        }
        updateWiki();
        selectWiki.change( updateWiki );
    };

    new SuggestionsView( new SuggestionsViewModel(), $('input.search'), $('ul.search-suggest'), $('#select-wiki') );
});
