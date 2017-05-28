messages = [
    'Bytes',
    'ControlMode',
    'Gps',
    'Motion',
    'Position',
    'TypedMessage',
    'Velocity',
];
module.exports = Object.assign({}, null);
messages.forEach((name) =>
    module.exports[name] = require(`./schemas/${name}_pb`)[name]);
