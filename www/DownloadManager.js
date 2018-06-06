var exec = require('cordova/exec');

exports.download = function(url, options, success, error) {
    options = options || {};
    exec(success, error, "DownloadManager", "download", [url, options]);
};
