messages = [
    'Gps',
    'Motion',
    'Position',
    'Velocity',
];
module.exports = Object.assign({}, null);
messages.forEach((name) =>
    module.exports[name] = require(`./schemas/${name}_pb`)[name]);
